package pro.carl.edu.sagan1.logic.compiling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.entity.RobotModels;
import pro.carl.edu.sagan1.logic.ConfigProperties;
import pro.carl.edu.sagan1.logic.Versions;
import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.parsing.ScriptParsingException;
import pro.carl.edu.sagan1.logic.parsing.ScriptLineParser;
import pro.carl.edu.sagan1.logic.parsing.SingleCommand;

import static pro.carl.edu.sagan1.logic.MasterMind.log;


/**
 * Stateful object that manages the compilation of the user program, the generation 
 * of the RP6 C program based on the template and the call to the GCC cross
 * compiler. 
 * 
 * @version 1.0.0
 * @since 0.0
 */
public class Rp6ProgramCompiler {
    
    static public final int EVENTID_COMPILING_STARTED=78323852;
    static public final int EVENTID_COMPILING_FAILED=781145383;
    static public final int EVENTID_COMPILING_SUCCEEDED=7811254;
    static public final int EVENTID_COMPILING_PARSINGERROR=785212055;
    static public final int EVENTID_COMPILING_CONFIGURATIONERROR=785212056;
    static public final int EVENTID_COMPILING_OUTPUT=785212057;
        
    /** This label indicated the point where the user program C lines 
    will be inserted into the template. */
    static private final String INSERTION_POINT_LABEL="/*{SAGAN1_COMMANDS_HERE}*/";
    
    /** Name of the RP6 HEX file in the output directory. */
    static private final String RP6_COMP_HEX_OUT="sagan1_out.hex";
    
    /** Name of the RP6 SAGAN source file in the output directory, written
     just to have the source available with the binary file. */
    static private final String RP6_COMP_SRC_OUT="sagan1_out.sagan1";
    
    /** Cross-compiling targert C file. Also defined in RP6Comp makefile. */
    static private final String RP6_COMP_SRC_C="sagan1_out.c";
    
    /** Defines the template C file where the users commands will be inserted.
    The file must be placed in the 'config.dir.rp6.compiling' directory.
    The inserting place must be marked with INSERTION_POINT_LABEL. */
    static private final String RP6_COMP_TEMPLATE_C="_sagan_1_template.c";
        
    /** Contains the user program code as text, line per line. */
    private List<String> programLines;
    
    /** Contains the user program as parsed and defined instruction objects, line per line. */
    private List<SingleCommand> programCommands;
    
    /** Line number where the last error has been detected. */
    private int lineInError;
    /** Last command in error. */
    private String commandInError;
    /** Error message describing the last error. */
    private String errorMessage;
    
    /** Robot currently selected by the user. This influences calibration parameters. */
    private Robot robot;
    
    /** The generated C program. */
    private StringBuilder cprogram;
    
    /** List of registered event listeners. */
    private List<ActionListener> eventListeners=new ArrayList<ActionListener>();
    
    
    
    /**
     * Initialising constructor.
     */
    public Rp6ProgramCompiler(List<String> progLines,Robot robot) {
        reset();
        this.programLines=progLines;
        this.robot=robot;
    }
    
    
    /**
     * Adds a curious observer.
     */
    public void addEventListener(ActionListener l) {
        eventListeners.add(l);
    }
    
    /**
     * Does the parsing, compilation, packing (and later transmission) of the user application.
     */
    public void start() {
    
        reset(); 
        
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_STARTED,null));
        
        boolean success=false;
        try {
            parseUserProgram();
            loadCTemplateFile();
            generateCProgramm();
            writeRp6CSourceFile();
            calibrateCompilationEnvironment();
            success=launchCompilation();
            if (success) copyHexFileToOut();
            if (success) writeSourceToOut();
        }
        catch(CompilationExecutionException e) {
            if (e.getActionEvent1()!=null) fireEvent(e.getActionEvent1());
            if (e.getActionEvent2()!=null) fireEvent(e.getActionEvent2());
            return;
        }
        
        if (success)
            fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_SUCCEEDED,null));
        else
            fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null));
    }
    
    
    /**
     * Modifies the calibration parameters of the RP6 base library to the selected 
     * robots settings.
     */
    private void calibrateCompilationEnvironment() throws CompilationExecutionException {
    
        if (!robot.getModelId().equalsIgnoreCase(RobotModels.RP6.getModelKey())) // TODO refactor this to use the enumeration, cf configuration parsing.
            return;
        
        String rp6configFileName=Configuration.getInstance().getProperty(ConfigProperties.RP6_RP6CONFIG_H);
        
        log("Model is RP6, calibrate by updating "+rp6configFileName);
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Model is RP6, calibrate by updating "+rp6configFileName));
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Set ROTATION_FACTOR="+robot.getRp6CalibrationRotationFactor()));
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Set ENCODER_RESOLUTION="+robot.getRp6CalibrationEncoderResolution()));
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Robot: "+robot.getKey()+"/"+robot.getModelId()+"/"+robot.getName()));
                
        
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        FileWriter fw=null;
        PrintWriter pw=null;
        
        StringBuilder sb=new StringBuilder();
        try {
            // Load RPConfig.h and updating it on the fly
            fileReader=new FileReader(rp6configFileName);
            bufferedReader=new BufferedReader(fileReader);
            
            String line=bufferedReader.readLine();
            while(line!=null) {
                
                if (line.toUpperCase().startsWith("#define ENCODER_RESOLUTION".toUpperCase()))
                    line="#define ENCODER_RESOLUTION "+robot.getRp6CalibrationEncoderResolution();
                else if (line.toUpperCase().startsWith("#define ROTATION_FACTOR".toUpperCase()))
                    line="#define ROTATION_FACTOR "+robot.getRp6CalibrationRotationFactor();
                
                sb.append(line).append("\n");
                line=bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
            fileReader=null;
            bufferedReader=null;
            
            // Write file back
            fw=new FileWriter(rp6configFileName,false);
            pw=new PrintWriter(fw);
            pw.println(sb.toString());
        }
        catch (IOException e){
            String err="I/O exception reading or writing RP6 library calibration: File:"+rp6configFileName+"/"+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (bufferedReader!=null) bufferedReader.close(); } catch(Exception e) {} 
            try { if (fileReader!=null) fileReader.close(); } catch(Exception e) {} 
            try { if (pw!=null) pw.close(); } catch(Exception ex) {}
            try { if (fw!=null) fw.close(); } catch(Exception ex) {}
        }
        
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"RP6 library calibrated for selected robot."));
    }
    
        
    /**
     * Launches the external cross-compiler in an external process and 
     * catches the stdout/stderr into the Sagan console.
     */
    private boolean launchCompilation() throws CompilationExecutionException {
        
        String compDir=Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_COMPILING);
        String userDir=System.getProperty("user.dir");
        
        // Constructs a process builder with the specified operating
        // system program and arguments.
        String procName=userDir+File.separator+compDir+File.separator+"build.bat";
        ProcessBuilder pb=new ProcessBuilder(procName);
        
        // Sets the PB working directory
        if (userDir==null)
            pb.directory(new File(compDir));
        else {
            if (userDir.endsWith(File.separator))
                pb.directory(new File(userDir+compDir));
            else
                pb.directory(new File(userDir+File.separator+compDir));
        }
        
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Start cross-compilation."));
        log("Set working executable to: "+procName);
        log("Set working directory to: "+pb.directory().getAbsolutePath());
        log("Starting process...");
        
        pb.redirectErrorStream(true);  // Capture messages sent to stderr
        
        // Starts a new process using the attributes of this process builder.
        Process shell;
        try {
            shell=pb.start();
        } 
        catch (IOException ex) {
            String err="I/O exception while starting the ProcessBuilder process: /"+ex.getMessage();
            log("I/O exception while starting the ProcessBuilder process",ex);
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        
        /*
         * Note: shell.waitFor() is not suitable for our purpose
         * since the process execution blocks when the process output (into stdout/stderr)
         * is not read and the buffer is full. For small commands, this isn't a problem 
         * but we have a huge output so we need to empty the buffer while executing.
         */
        
        // Captures the output from the command
        InputStream shellIn=shell.getInputStream(); 
        BufferedInputStream bufShellIn=new BufferedInputStream(shellIn);
        InputStreamReader shellInReader=new InputStreamReader(bufShellIn);
        BufferedReader reader=new BufferedReader(shellInReader);
        
        try {
            shellIn=shell.getInputStream(); 
            bufShellIn=new BufferedInputStream(shellIn);
            shellInReader=new InputStreamReader(bufShellIn);
            reader=new BufferedReader(shellInReader);
        
            while(true) {
                String line = reader.readLine();
                if (line==null) break;
                fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,line));
            }		
        }
        catch (IOException ex) {
            String err="I/O exception while reading compiler process output: "+ex.getMessage();
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { reader.close(); } catch (IOException e) {}
            try { shellInReader.close(); } catch (IOException e) {}
            try { bufShellIn.close(); } catch (IOException e) {}
            try { shellIn.close(); } catch (IOException e) {}
        }
        
        log("Process exit status: "+shell.exitValue());
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Process exit status: "+shell.exitValue()));
        
        return (shell.exitValue()==0);
    }
   
    
    /**
     * Writes the generated source code into the compilation directory.
     */
    private void writeRp6CSourceFile() throws CompilationExecutionException {
        
        FileWriter fw=null;
        PrintWriter pw=null;
        
        String fileName=Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_COMPILING)+
                        File.separator+RP6_COMP_SRC_C;
        
        try {
            fw=new FileWriter(fileName);
            pw=new PrintWriter(fw);
  
            pw.println(cprogram.toString());
        } 
        catch (IOException e){
            String err="I/O exception writing generated C program: File:"+fileName+"/"+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (pw!=null) pw.close(); } catch(Exception ex) {}
            try { if (fw!=null) fw.close(); } catch(Exception ex) {}
        }
        
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Store generated C file: "+fileName));
    }
    
    
    /**
     * Loads the template RP6 C file into memory buffer.
     */
    private void loadCTemplateFile() throws CompilationExecutionException {
    
        String text;
        BufferedReader rd=null;
        FileReader fr=null;
        
        String lf=System.getProperty("line.separator");
        
        String templateName=Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_COMPILING)+
                            File.separator+RP6_COMP_TEMPLATE_C;
               
        cprogram=new StringBuilder();
        
        try {
            fr=new FileReader(templateName);
            rd=new BufferedReader(fr);
            while ( (text=rd.readLine()) != null) 
                cprogram.append(text).append(lf);
        }
        catch(IOException e) {
            String err="I/O exception during template file opening: "+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (rd!=null) rd.close(); } catch(Exception ex) {}
            try { if (fr!=null) fr.close(); } catch(Exception ex) {}
        }
        
        int insertPointPos=cprogram.indexOf(INSERTION_POINT_LABEL);
        if (insertPointPos==-1) {
            String err="The RP6 C template file doesn't contain the insertion label: "+INSERTION_POINT_LABEL+", File:"+templateName;
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
    }
    
    
    /**
     * Generates the C program from the user programs instructions and inserts it
     * into the template.
     */
    private void generateCProgramm() throws CompilationExecutionException {
        
        StringBuilder prog=new StringBuilder("\n/* generated by ");
        
        prog.append(Versions.APP_TITLE).append(", ").append(Versions.APP_VERSION);
        prog.append(" on ").append(new Date().toString()).append("*/\n\n");
        
        for (SingleCommand c:programCommands) {
            if (c!=null)
                c.appendRP6CommandString(prog).append("\n");
        }
  
        // Insert into template code
        int insertPointPos=cprogram.indexOf(INSERTION_POINT_LABEL);
        cprogram.replace(insertPointPos, insertPointPos+INSERTION_POINT_LABEL.length(),prog.toString());
    }
    
    
    /**
     * Parses the user program into a list of defined instructions.
     */
    private void parseUserProgram() throws CompilationExecutionException {
    
        ScriptLineParser scriptParser=new ScriptLineParser();
        int currentLine=0;
        
        programCommands=new ArrayList<SingleCommand>();
        try {
            for (String currentCommandText:programLines) {
                programCommands.add(scriptParser.parse(currentCommandText,currentLine));
                currentLine++;
            } 
        }
        catch (ScriptParsingException ex) {
            // Syntax error, stop!
            lineInError=ex.getPos();
            commandInError=ex.getLine();
            errorMessage=ex.getMessage();
            ActionEvent ae=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_PARSINGERROR,
                                            (Integer.toString(lineInError+1)+" ["+commandInError+"] "+errorMessage));
            throw new CompilationExecutionException(ae);
        }
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,"User program parsed successfully."));
    }
    
    
    /**
     * Copies a file from the source to the destination directory.
     */
    private void copyHexFileToOut() throws CompilationExecutionException {
        
        File f1=null;
        File f2=null;
        InputStream in=null;
        OutputStream out=null;
        
        String srcDir=Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_COMPILING);
        String destDir=Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_HEXDEST);
        
        try {
            if (srcDir.endsWith(File.separator)) f1=new File(srcDir+RP6_COMP_HEX_OUT); 
                else f1=new File(srcDir+File.separator+RP6_COMP_HEX_OUT);
            if (destDir.endsWith(File.separator)) f2=new File(destDir+RP6_COMP_HEX_OUT); 
                else f2=new File(destDir+File.separator+RP6_COMP_HEX_OUT);
            
            log("Copy file: "+f1.getAbsolutePath()+" to "+f2.getAbsolutePath());
            
            in=new FileInputStream(f1);
            out=new FileOutputStream(f2);

            byte[] buf=new byte[1024];
            int len;
            while ((len=in.read(buf)) > 0)
                out.write(buf,0,len);
        }

        catch(FileNotFoundException ex){
            String err="File not found while copying compiled HEX file to output directory: "+ex.getMessage();
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }        catch(IOException e) {
            String err="I/O exception during template file opening: "+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (out!=null) out.close(); } catch(Exception ex) {}
            try { if (in!=null) in.close(); } catch(Exception ex) {}
        }
        
        fireEvent(new ActionEvent(this,Rp6ProgramCompiler.EVENTID_COMPILING_OUTPUT,
                                    "Final cross-compiled user program HEX file: "+
                                    f2.getAbsolutePath()));
    }   
    
    
    /**
     * Writes the sources sagan script into the output directory in order to have
     * the HEX bundled with the HEX file.
     */
    @SuppressWarnings("CallToThreadDumpStack")
    private void writeSourceToOut() {
        
        FileWriter fileWriter=null;
        BufferedWriter bufferedWriter=null;
        String fileName;
        
        String destDir=Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_HEXDEST);
        if (destDir.endsWith(File.separator)) fileName=destDir+RP6_COMP_SRC_OUT; else fileName=destDir+File.separator+RP6_COMP_SRC_OUT;
        
        try {
            fileWriter=new FileWriter(fileName,false);
            bufferedWriter=new BufferedWriter(fileWriter);
            for (String s:programLines) {
                bufferedWriter.write(s);
                bufferedWriter.write("\n");
            }
            log("Write "+RP6_COMP_HEX_OUT+" to output directory.");
        }
        catch(IOException e) {
            log("Unable to write "+RP6_COMP_HEX_OUT+" to output directory. Ignoring error. "+e.getMessage());
        }
        finally {
            try { if (bufferedWriter!=null) bufferedWriter.close(); } catch(Exception e) { e.printStackTrace(); } 
            try { if (fileWriter!=null) fileWriter.close(); } catch(Exception e) { e.printStackTrace(); } 
        }
    }
    
    
    /**
     * Fires the passed event to all listeners.
     */
    private void fireEvent(ActionEvent event) {
        for (ActionListener l:eventListeners)
            l.actionPerformed(event);
    }
      
    
    /**
     * Resets the class into a state as required to (re)start program
     * compilation.
     */
    private void reset() {
        lineInError=-1;
        commandInError="";
        programCommands=null;
        errorMessage=null;
        cprogram=null;
        // Don't reset constructor sets.
    }
   
}
    
    
