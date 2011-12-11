package pro.carl.edu.sagan1.gui.shape.robot;

import java.awt.Graphics2D;

import pro.carl.edu.sagan1.entity.VehicleState;

/**
 *
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 * @author Tasha CARL
 */
public interface RobotShape {
    
    /**
     * Draws the robot shape.
     */
    public void draw(double aspectFactor,int x0,int y0,VehicleState pos,Graphics2D g);
    
}
