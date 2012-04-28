package pro.carl.edu.sagan1.logic.compiling;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import pro.carl.edu.sagan1.entity.Robot;

import pro.carl.edu.sagan1.logic.ConfigProperties;
import pro.carl.edu.sagan1.logic.Configuration;


import static pro.carl.edu.sagan1.logic.MasterMind.log;
import pro.carl.edu.sagan1.logic.parsing.Commands;
import pro.carl.edu.sagan1.logic.parsing.SingleCommand;


/**
 * Sub-class of the ProgrammCompiler class used to configured the compiler
 * to a specific compilation environment, in this case the Arexx RP6 setting.
 * 
 * @see ProgramCompiler
 * 
 * @version 1.1.0
 * @since 0.0
 */
public class Rp6ProgramCompiler extends ProgramCompiler {
    
    /** Name of the RP6 HEX file in the output directory. */
    static private final String RP6_COMP_HEX_OUT="sagan1_out.hex";
   
    /** Cross-compiling targert C file. Also defined in RP6Comp makefile. */
    static private final String RP6_COMP_SRC_C="sagan1_out.c";
    
    /** Defines the template C file where the users commands will be inserted.
    The file must be placed in the 'config.dir.rp6.compiling' directory.
    The inserting place must be marked with INSERTION_POINT_LABEL. */
    static private final String RP6_COMP_TEMPLATE_C="_sagan_1_template.c";
        
    
    
    
    /**
     * @see ProgramCompiler#doGetTemplateFileName() 
     */
    @Override
    protected String doGetTemplateFileName() {
        return RP6_COMP_TEMPLATE_C;
    } 
    
    
    /**
     * @see ProgramCompiler#doGetOutputSourceFilename() 
     */
    @Override
    protected String doGetOutputSourceFilename() {
        return RP6_COMP_SRC_C;
    }
        
    
    /**
     * @see ProgramCompiler#doGetCrossCompilingDirectory() 
     */
    @Override
    protected String doGetCrossCompilingDirectory() {
        return Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_COMPILING);
    }
    
    
    /**
     * @see ProgramCompiler#doGetCrossCompilingHexTargetDirectory() 
     */
    @Override
    protected String doGetCrossCompilingHexTargetDirectory() {
        return Configuration.getInstance().getProperty(ConfigProperties.RP6_DIR_HEXDEST);
    }
      
    
    /**
     * @see ProgramCompiler#doGetCrossCompilingHexTargetFilename() 
     */
    @Override
    protected String doGetCrossCompilingHexTargetFilename() {
        return RP6_COMP_HEX_OUT;
    }
    
    
    /**
     * Returns the cross-language instruction for the command when target is the 
     * RP6 robot system.
     * 
     * @see ProgramCompiler#doGetSpecificCommandString(pro.carl.edu.sagan1.logic.parsing.SingleCommand) 
     */
    @Override
    protected String doGetSpecificCommandString(SingleCommand singleCommandToExec,Robot robot) {

        Commands command=singleCommandToExec.getCommandInstruction();
        int distance=singleCommandToExec.getDistance();
        int degrees=singleCommandToExec.getDegrees();
        int time=singleCommandToExec.getTime();
        
        int rotationSpeed=Configuration.getInstance().getRp6RotationSpeed();
        int linemoveSpeed=Configuration.getInstance().getRp6LineMoveSpeed();
        
        if (command.isMetaCommand()) 
            return "";
        
        StringBuilder prog=new StringBuilder(200);
        if (command.equals(Commands.TURNRIGHT)) {
            prog.append("rotate(").append(rotationSpeed).append(",RIGHT,").append(degrees).append(",true);");
        }
        else if (command.equals(Commands.TURNLEFT)) {
            prog.append("rotate(").append(rotationSpeed).append(",LEFT,").append(degrees).append(",true);");
        }
        else if (command.equals(Commands.FORWARD)) {
            prog.append("move(").append(linemoveSpeed).append(",FWD,DIST_MM(").append(distance).append("),true);");        
        }
        else if (command.equals(Commands.BACKWARD)) {
            prog.append("move(").append(linemoveSpeed).append(",BWD,DIST_MM(").append(distance).append("),true);");
        }
        else if (command.equals(Commands.SENDSIGNAL)) {
            prog.append("signal();");
        }
        else if (command.equals(Commands.WAIT)) {
            prog.append("mSleep(").append(time>0 ? time:1000).append(");");
        }
        return prog.toString();
    }
    
    
    /**
     * Modifies the calibration parameters of the RP6 base library to the selected 
     * robots settings.
     * 
     * @see ProgramCompiler#doCalibrateCompilationEnvironment(pro.carl.edu.sagan1.entity.Robot) 
     */
    @Override
    protected void doCalibrateCompilationEnvironment(Robot robot) throws CompilationExecutionException {
    
        String rp6configFileName=Configuration.getInstance().getProperty(ConfigProperties.RP6_RP6CONFIG_H);
        
        log("Model is RP6, calibrate by updating "+rp6configFileName);
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Model is RP6, calibrate by updating "+rp6configFileName));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Set ROTATION_FACTOR="+robot.getRp6CalibrationRotationFactor()));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Set ENCODER_RESOLUTION="+robot.getRp6CalibrationEncoderResolution()));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Robot: "+robot.getKey()+"/"+robot.getModelId()+"/"+robot.getName()));
                
        
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
            ActionEvent ae1=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR,err);
            ActionEvent ae2=new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_FAILED,null);
            throw new CompilationExecutionException(ae1,ae2);
        }
        finally {
            try { if (bufferedReader!=null) bufferedReader.close(); } catch(Exception e) {} 
            try { if (fileReader!=null) fileReader.close(); } catch(Exception e) {} 
            try { if (pw!=null) pw.close(); } catch(Exception ex) {}
            try { if (fw!=null) fw.close(); } catch(Exception ex) {}
        }
        
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"RP6 library calibrated for selected robot."));
    }

    
    /**
     * @see ProgramCompiler#beforeCompilationOutput(pro.carl.edu.sagan1.entity.Robot) 
     */
    @Override
    protected void beforeCompilationOutput(Robot robot) {
    }
}
    
    
