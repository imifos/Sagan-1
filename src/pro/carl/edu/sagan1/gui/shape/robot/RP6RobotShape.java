package pro.carl.edu.sagan1.gui.shape.robot;

import java.awt.Graphics2D;

import pro.carl.edu.sagan1.entity.RobotModels;
import pro.carl.edu.sagan1.entity.VehicleState;

/**
 * Draws the RP6 robot shape into the passed graphic context.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class RP6RobotShape implements RobotShape {
    
    /** The SimpleRobotShape is infact the RP6 shape. */
    SimpleRobotShape srs=SimpleRobotShape.getInstance();
    
    /** ID of this robot shape covered by this class. */
    static public final String MODELID=RobotModels.RP6.getModelKey();
    
    /**
     * Returns a new instance of this robot shape.
     */
    static public RP6RobotShape getInstance() {
        return new RP6RobotShape();
    }
    
    /**
     * Package-private constructor to force usage of factory method.
     */
    RP6RobotShape() {
    }
        
    /**
     * Draws the robot shape.
     * @param aspectFactor factor to apply t0 positions in mm to transform it to pixels.
     */
    @Override
    public void draw(double aspectFactor,int x0,int y0,VehicleState pos,Graphics2D g) {
        srs.draw(aspectFactor,x0,y0,pos,g);
    }
}
