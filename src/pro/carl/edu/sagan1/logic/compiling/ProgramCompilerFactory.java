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
