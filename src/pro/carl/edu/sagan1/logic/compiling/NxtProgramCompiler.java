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
import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.logic.ConfigProperties;
import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.parsing.Commands;
import pro.carl.edu.sagan1.logic.parsing.SingleCommand;


/**
 * Sub-class of the ProgrammCompiler class used to configured the compiler
 * to a specific compilation environment, in this case the Lego Mindstrom NXT.
 * 
 * @see ProgramCompiler
 * 
 * @version 1.0.0
 * @since 0.0
 */
public class NxtProgramCompiler extends ProgramCompiler {

   
    /** Cross-compiling targert C file. Also defined in RP6Comp makefile. */
    static private final String COMP_SRC_NXC="sagan1_out.nxc";
    
    /** Defines the template C file where the users commands will be inserted.
    The file must be placed in the 'config.dir.rp6.compiling' directory.
    The inserting place must be marked with INSERTION_POINT_LABEL. */
    static private final String COMP_TEMPLATE_NXC="_sagan_1_template.nxc";
        
    
    /**
     * @see ProgramCompiler#doGetTemplateFileName() 
     */
    @Override
    protected String doGetTemplateFileName() {
        return COMP_TEMPLATE_NXC;
    } 
    
    
    /**
     * @see ProgramCompiler#doGetOutputSourceFilename() 
     */
    @Override
    protected String doGetOutputSourceFilename() {
        return COMP_SRC_NXC;
    }
        
    
    /**
     * @see ProgramCompiler#doGetCrossCompilingDirectory() 
     */
    @Override
    protected String doGetCrossCompilingDirectory() {
        return Configuration.getInstance().getProperty(ConfigProperties.NXT_DIR_COMPILING);
    }
    
    
    /**
     * @see ProgramCompiler#doGetCrossCompilingHexTargetDirectory() 
     */
    @Override
    protected String doGetCrossCompilingHexTargetDirectory() {
        return null;
    }
      
    
    /**
     * @see ProgramCompiler#doGetCrossCompilingHexTargetFilename() 
     */
    @Override
    protected String doGetCrossCompilingHexTargetFilename() {
        return null;
    }
    
    
    /**
     * Modifies the calibration parameters of cross compilation environment.
     * 
     * @see ProgramCompiler#doCalibrateCompilationEnvironment(pro.carl.edu.sagan1.entity.Robot) 
     */
    @Override
    protected void doCalibrateCompilationEnvironment(Robot robot) throws CompilationExecutionException {
        
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Model is LEGO NXT"));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"CAL_TIME_PER_MM="+robot.getNxtCalibrationTimePerMillimeter()));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"CAL_TIME_PER_DEGREE="+robot.getNxtCalibrationTimePerDegree()));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"USE_COMPASS="+robot.isNxtUsesCompassSensor()));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"USE_PRECISE_ROTATION="+robot.isNxtUsesHighPrecisionCompassRotation()));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Robot: "+robot.getKey()+"/"+robot.getModelId()+"/"+robot.getName()));
    }

    
    /**
     * Returns the cross-language instruction for the command when target is the 
     * LEGO NXT robot system.
     * 
     * @see ProgramCompiler#doGetSpecificCommandString(pro.carl.edu.sagan1.logic.parsing.SingleCommand) 
     */
    @Override
    protected String doGetSpecificCommandString(SingleCommand singleCommandToExec,Robot robot) {
        
        Commands command=singleCommandToExec.getCommandInstruction();
        int distance=singleCommandToExec.getDistance();
        int degrees=singleCommandToExec.getDegrees();
        int time=singleCommandToExec.getTime();
        
        int moveSpeed=Configuration.getInstance().getNxtLineMoveSpeed();
        
        double msAhead=robot.getNxtCalibrationTimePerMillimeter();
        double msRotate=robot.getNxtCalibrationTimePerDegree();
        boolean usesCompass=robot.isNxtUsesCompassSensor();
        boolean usesHPRot=robot.isNxtUsesHighPrecisionCompassRotation();
                
        if (command.isMetaCommand()) 
            return "";
                
        StringBuilder prog=new StringBuilder(200);
        if (command.equals(Commands.TURNRIGHT)) {
            if (!usesCompass) {
                prog.append("if (stopIt!=1) { OnFwdSync(OUT_BC,").append(moveSpeed).append(",100);  ").
                     append("Wait(").append((int)(msRotate*((double)degrees))).append("); Off(OUT_BC); }");
                // Crutial to perfom calculations as floating point
            }
            else {
                // The rotation logic is actually implemented in the NXC template.
                if (usesHPRot)
                    prog.append("turnRightPrecise(").append(degrees).append(");");
                else prog.append("turnRight(").append(degrees).append(");");
            }
        }
        else if (command.equals(Commands.TURNLEFT)) {
            if (!usesCompass) {
                prog.append("if (stopIt!=1) { OnFwdSync(OUT_BC,").append(moveSpeed).append(",-100);  ").
                     append("Wait(").append((int)(msRotate*((double)degrees))).append("); Off(OUT_BC); }");
            }
            else {
                if (usesHPRot)
                    prog.append("turnLeftPrecise(").append(degrees).append(");");
                else prog.append("turnLeft(").append(degrees).append(");");
            }
        }
        else if (command.equals(Commands.FORWARD)) {
            prog.append("if (stopIt!=1) { OnFwdSync(OUT_BC,").append(moveSpeed).append(",0);  ").
                 append("Wait(").append((int)(msAhead*((double)distance))).append("); Off(OUT_BC); }");
        }
        else if (command.equals(Commands.BACKWARD)) {
            prog.append("if (stopIt!=1) { OnRevSync(OUT_BC,").append(moveSpeed).append(",0);  ").
                 append("Wait(").append((int)(msAhead*((double)distance))).append("); Off(OUT_BC); }");
        }
        else if (command.equals(Commands.SENDSIGNAL)) {
            prog.append("if (stopIt!=1) { Off(OUT_BC); signal(); }");
        }
        else if (command.equals(Commands.WAIT)) {
            prog.append("if (stopIt!=1) { Off(OUT_BC); Wait(").append(time>0 ? time:1000).append("); }");
        }
        return prog.toString();
    }
    
        
    /**
     * @see ProgramCompiler#beforeCompilationOutput(pro.carl.edu.sagan1.entity.Robot) 
     */
    @Override
    protected void beforeCompilationOutput(Robot robot) {
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,""));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"--------------------------------------------------------"));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"Please be sure to connect your LEGO MINDSTORM NEXT robot "));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"to the USB port before compiling."));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"The robot makes a sound after successful transmission."));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,"--------------------------------------------------------"));
        fireEvent(new ActionEvent(this,ProgramCompiler.EVENTID_COMPILING_OUTPUT,""));
    }
    
}
    
    
