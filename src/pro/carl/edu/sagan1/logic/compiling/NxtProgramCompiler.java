package pro.carl.edu.sagan1.logic.compiling;


import java.awt.event.ActionEvent;
import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.logic.ConfigProperties;
import pro.carl.edu.sagan1.logic.Configuration;
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
        // Nothing to do here
    }

    /**
     * @see ProgramCompiler#doGetSpecificCommandString(pro.carl.edu.sagan1.logic.parsing.SingleCommand) 
     */
    @Override
    protected String doGetSpecificCommandString(SingleCommand singleCommandToExec,Robot robot) {
        return singleCommandToExec.getNXTCommandString(robot);
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
    
    
