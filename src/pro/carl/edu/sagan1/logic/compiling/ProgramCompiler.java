/*
* Sagan-1 Robot Simulator
* -----------------------
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* A copy of the GNU General Public License can be found here:
* http://www.gnu.org/licenses/.
*
* Author:
* Tasha CARL, 2011, http://lucubratory.eu / http://sagan-project.eu
*/
package pro.carl.edu.sagan1.logic.compiling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import pro.carl.edu.sagan1.entity.Robot;

import pro.carl.edu.sagan1.logic.Versions;
import pro.carl.edu.sagan1.logic.parsing.ScriptLineParser;
import pro.carl.edu.sagan1.logic.parsing.ScriptParsingException;
import pro.carl.edu.sagan1.logic.parsing.SingleCommand;

import static pro.carl.edu.sagan1.logic.MasterMind.log;


/**
 * Stateful object that manages the compilation of the user program, the generation 
 * of the output program based on the template, the call to the cross
 * compiler and optionally the upload to the robot.
 * 
 * @author @Imifos
 * @version 1.0.0
 * @since 1.0.0
 */
abstract public class ProgramCompiler {
    
    static public final int EVENTID_COMPILING_STARTED=78323852;
    static public final int EVENTID_COMPILING_FAILED=781145383;
    static public final int EVENTID_COMPILING_SUCCEEDED=7811254;
    static public final int EVENTID_COMPILING_PARSINGERROR=785212055;
    static public final int EVENTID_COMPILING_CONFIGURATIONERROR=785212056;
    static public final int EVENTID_COMPILING_OUTPUT=785212057;
    
    /** This label indicated the point where the user program lines 
    will be inserted into the template. */
    static private final String INSERTION_POINT_LABEL="/*{SAGAN1_COMMANDS_HERE}*/";
        
     /** Name of the SAGAN source file in the output directory, written
     just to have the source available with the binary file. */
    static private final String AFTER_COMP_SRC_OUT="sagan1_out.sagan1";
    
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
    
    /** The generated output program. */
    private StringBuilder outputProgram;
    
    /** List of registered event listeners. */
    private List<ActionListener> eventListeners=new ArrayList<ActionListener>();
    
    
    // =======================================================================
    // ENVIRONMENT SPECIFIC SUB-CLASS INTERFACE
    // =======================================================================
    
    
    /**
     * Modifies the calibration parameters of the target compilation environment 
     * to the selected robots settings.
     */
    abstract protected void doCalibrateCompilationEnvironment(Robot robot) throws CompilationExecutionException;
    
    /**
     * Returns the template file used as base for the compiled Sagan program.
     */
    protected abstract String doGetTemplateFileName();

    /**
     * Returns the single filename of the resulting sagan compilation,
     * effectively a merge between "doGetTemplateFileName" and the generated 
     * sagan commands.
     */
    protected abstract String doGetOutputSourceFilename();
    
    /**
     * Returns the destination directory for cross-compiling. Represents
     * a temporary directory and deleting files here shouldn't be a problem.
     */
    protected abstract String doGetCrossCompilingDirectory();
    
    /**
     * Returns the cross-compiled HEX file's destination directory or NULL if 
     * a copy is not needed. The latter case could occur when the compilation 
     * process load the file already and no external loader is involved (like for RP6).
     */
    abstract protected String doGetCrossCompilingHexTargetDirectory();

    /* Returns the cross-compiled HEX file's name (no path) or NULL if 
     * a copy is not needed. The latter case could occur when the compilation 
     * process load the file already and no external loader is involved (like for RP6).
     */
    abstract protected String doGetCrossCompilingHexTargetFilename();
 
    /**
     * Passes the current command to the environment specific sub-class 
     * in order to let them determine the effective command to insert in the
     * programm flow.
     */
    abstract protected String doGetSpecificCommandString(SingleCommand singleCommandToExec,Robot robot);
    
    /**
     * Allows to write some instructions into the output log depending on the
     * used robot (type).
     */
    abstract protected void beforeCompilationOutput(Robot robot);
    
    // =======================================================================
    // COMMON COMPILER IMPLEMENTATION
    // =======================================================================
    
    
    /**
     * Sets up the compiler object.
     */
    public void setupCompiler(List<String> progLines,Robot robot) {
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
     * Does the parsing, compilation, packing (and later transmission) of 
     * the user application.
     */
    public void start() {
    
        reset(); 
        
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_STARTED,null));
        
        boolean success;
        try {
            beforeCompilationOutput(robot);
            parseUserProgram();
            loadTemplateFile();
            generateOutputProgramm(robot);
            writeGeneratedSourceFile();
            doCalibrateCompilationEnvironment(robot);
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
            fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_SUCCEEDED,null));
        else
            fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null));
    }
    
    
        
    /**
     * Launches the external cross-compiler in an external process and 
     * catches the stdout/stderr into the Sagan console.
     */
    private boolean launchCompilation() throws CompilationExecutionException {
        
        String compDir=doGetCrossCompilingDirectory();
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
        
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Start cross-compilation."));
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
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        
        /*
         * Note: shell.waitFor() is not suitable for our purpose
         * since the process execution blocks when the process output (into stdout/stderr)
         * is not read and the buffer is full. For small commands, this isn't a problem 
         * but we have a huge output so we need to empty the buffer while executing.
         */
        /*
         * Note: since we are not creating a parallel thread for this operation, 
         * the GUI will be blocked. This is (currently) an acceptable tradeoff
         * for complexity.
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
                fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,line));
            }		
        }
        catch (IOException ex) {
            String err="I/O exception while reading compiler process output: "+ex.getMessage();
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { reader.close(); } catch (IOException e) {}
            try { shellInReader.close(); } catch (IOException e) {}
            try { bufShellIn.close(); } catch (IOException e) {}
            try { shellIn.close(); } catch (IOException e) {}
        }
        
        log("Process exit status: "+shell.exitValue());
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Process exit status: "+shell.exitValue()));
        
        return (shell.exitValue()==0);
    }
   
    
    /**
     * Writes the generated source code into the compilation directory.
     */
    private void writeGeneratedSourceFile() throws CompilationExecutionException {
        
        FileWriter fw=null;
        PrintWriter pw=null;
        
        String absoluteFilename;
        if (doGetCrossCompilingDirectory().endsWith(File.separator)) 
            absoluteFilename=doGetCrossCompilingDirectory()+doGetOutputSourceFilename(); 
        else absoluteFilename=doGetCrossCompilingDirectory()+File.separator+doGetOutputSourceFilename();  
   
        try {
            fw=new FileWriter(absoluteFilename);
            pw=new PrintWriter(fw);
  
            pw.println(outputProgram.toString());
        } 
        catch (IOException e){
            String err="I/O exception writing generated program: File:"+doGetOutputSourceFilename()+"/"+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (pw!=null) pw.close(); } catch(Exception ex) {}
            try { if (fw!=null) fw.close(); } catch(Exception ex) {}
        }
        
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,
                        "Store generated output source file: "+doGetOutputSourceFilename()));
    }
    
    
    /**
     * Loads the template file into memory buffer.
     */
    private void loadTemplateFile() throws CompilationExecutionException {
    
        String text;
        BufferedReader rd=null;
        FileReader fr=null;
        
        String lf=System.getProperty("line.separator");
        
        String templateName;
        if (doGetCrossCompilingDirectory().endsWith(File.separator)) 
            templateName=doGetCrossCompilingDirectory()+doGetTemplateFileName(); 
        else templateName=doGetCrossCompilingDirectory()+File.separator+doGetTemplateFileName();  

        outputProgram=new StringBuilder();
        
        try {
            fr=new FileReader(templateName);
            rd=new BufferedReader(fr);
            while ( (text=rd.readLine()) != null) 
                outputProgram.append(text).append(lf);
        }
        catch(IOException e) {
            String err="I/O exception during template file opening: "+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (rd!=null) rd.close(); } catch(Exception ex) {}
            try { if (fr!=null) fr.close(); } catch(Exception ex) {}
        }
        
        int insertPointPos=outputProgram.indexOf(INSERTION_POINT_LABEL);
        if (insertPointPos==-1) {
            String err="The template file doesn't contain the insertion label: "+INSERTION_POINT_LABEL+", File:"+templateName;
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
    }
    
    
    /**
     * Generates the output program (C,NXC) from the user programs instructions and inserts it
     * into the template.
     */
    private void generateOutputProgramm(Robot robot) throws CompilationExecutionException {
        
        StringBuilder prog=new StringBuilder("\n/* generated by ");
        
        prog.append(Versions.APP_TITLE).append(", ").append(Versions.APP_VERSION);
        prog.append(" on ").append(new Date().toString()).append(" http://sagan.be */\n\n");
        
        for (SingleCommand c:programCommands) {
            if (c!=null) {
                // Each "SingleCommand" knows how it has to be written into 
                // a destination source file in order to be executed correctly
                // on the target robot.
                prog.append(doGetSpecificCommandString(c,robot)).append("\n");
            }
        }
  
        // Insert into template code
        int insertPointPos=outputProgram.indexOf(INSERTION_POINT_LABEL);
        outputProgram.replace(insertPointPos, insertPointPos+INSERTION_POINT_LABEL.length(),prog.toString());
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
            ActionEvent ae=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_PARSINGERROR,
                                            (Integer.toString(lineInError+1)+" ["+commandInError+"] "+errorMessage));
            throw new CompilationExecutionException(ae);
        }
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"User program parsed successfully."));
    }
    
    
    /**
     * Copies a the cross-compiled file from the source to the destination 
     * directory, if required (cf compiler configuration).
     */
    private void copyHexFileToOut() throws CompilationExecutionException {
        
        File f1;
        File f2=null;
        InputStream in=null;
        OutputStream out=null;
        
        String srcDir=doGetCrossCompilingDirectory();
        String destDir=doGetCrossCompilingHexTargetDirectory();
        String destFileName=doGetCrossCompilingHexTargetFilename();
        
        if (destDir==null || destFileName==null) 
            return; // no copy needed.
        
        try {
            if (srcDir.endsWith(File.separator)) f1=new File(srcDir+destFileName); 
                else f1=new File(srcDir+File.separator+destFileName);
            if (destDir.endsWith(File.separator)) f2=new File(destDir+destFileName); 
                else f2=new File(destDir+File.separator+destFileName);
            
            log("Copy file: "+f1.getAbsolutePath()+" to "+f2.getAbsolutePath());
            
            in=new FileInputStream(f1);
            out=new FileOutputStream(f2);

            byte[] buf=new byte[1024];
            int len;
            while ((len=in.read(buf)) > 0)
                out.write(buf,0,len);
        }
        catch(FileNotFoundException ex){
            String err="File not found while copying compiled file to output directory: "+ex.getMessage();
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }        
        catch(IOException e) {
            String err="I/O exception during template file opening: "+e.getMessage();
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (out!=null) out.close(); } catch(Exception ex) {}
            try { if (in!=null) in.close(); } catch(Exception ex) {}
        }
        
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,
                                    "Final cross-compiled user program file: "+
                                    f2.getAbsolutePath()));
    }   
    
    
    /**
     * Writes the sources sagan script into the output directory in order to have
     * the cross compiled file bundled with the source file.
     */
    @SuppressWarnings("CallToThreadDumpStack")
    private void writeSourceToOut() {
        
        FileWriter fileWriter=null;
        BufferedWriter bufferedWriter=null;
        String fileName;
        
        String destDir=doGetCrossCompilingHexTargetDirectory();
        if (destDir==null) destDir=doGetCrossCompilingDirectory();
        if (destDir==null) return; 
        
        if (destDir.endsWith(File.separator)) fileName=destDir+AFTER_COMP_SRC_OUT; 
                else fileName=destDir+File.separator+AFTER_COMP_SRC_OUT;
        
        try {
            fileWriter=new FileWriter(fileName,false);
            bufferedWriter=new BufferedWriter(fileWriter);
            for (String s:programLines) {
                bufferedWriter.write(s);
                bufferedWriter.write("\n");
            }
            log("Write "+AFTER_COMP_SRC_OUT+" to output directory.");
        }
        catch(IOException e) {
            log("Unable to write "+AFTER_COMP_SRC_OUT+" to output directory. Ignoring error. "+e.getMessage());
        }
        finally {
            try { if (bufferedWriter!=null) bufferedWriter.close(); } catch(Exception e) { e.printStackTrace(); } 
            try { if (fileWriter!=null) fileWriter.close(); } catch(Exception e) { e.printStackTrace(); } 
        }
    }
    
    
    /**
     * Fires the passed event to all listeners.
     */
    protected void fireEvent(ActionEvent event) {
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
        outputProgram=null;
        // Don't reset constructor sets.
    }

 
   
}