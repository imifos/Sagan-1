package pro.carl.edu.sagan1.logic.compiling;

import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.gui.shape.robot.LegoRover1Shape;
import pro.carl.edu.sagan1.gui.shape.robot.RP6RobotShape;

/**
 * Registry of compiler classes and robot type associations.
 * 
 * @version 1.0.0
 * @since 0.9
 */
public class ProgramCompilerFactory {
    
    /**
     * Returns the fresh compiler for the passed robot type, or NULL if no 
     * adequate compiler is available.
     */
    public static ProgramCompiler getCompiler(Robot robot) {
    
        if (robot.getModelId().equals(RP6RobotShape.MODELID))
            return new Rp6ProgramCompiler();
        
        if (robot.getModelId().equals(LegoRover1Shape.MODELID))
            return new NxtProgramCompiler();
        
        // No compiler available
        return null;
    }
    
    
}
