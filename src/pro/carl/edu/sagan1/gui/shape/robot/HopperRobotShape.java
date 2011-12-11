package pro.carl.edu.sagan1.gui.shape.robot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import pro.carl.edu.sagan1.entity.RobotModels;
import pro.carl.edu.sagan1.entity.VehicleState;

/**
 * Draws the HOPPER robot shape into the passed graphic context.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class HopperRobotShape implements RobotShape {
    
    /** ID of this robot shape covered by this class. */
    static public final String MODELID=RobotModels.HOPPER.getModelKey();
    
    /** Globalised "aspectFactor". @see pro.carl.edu.sagan1.gui.viewcon.SimulationJPanel */
    private double aspectFactor=0;
    
    /**
     * Returns a new instance of this robot shape.
     */
    static public HopperRobotShape getInstance() {
        return new HopperRobotShape();
    }
    
    /**
     * Package-private constructor to force usage of factory method.
     */
    HopperRobotShape() {
    }
    
    
    /**
     * Converts the passed mm value to pixel using the pre-calculated aspect factor.
     * @see pro.carl.edu.sagan1.gui.viewcon.SimulationJPanel#computeDisplayParameters(java.awt.Graphics2D) 
     */
    private int mm2px(double mm) {
        return (int)(mm*aspectFactor);
    }

    
    /**
     * Draws the robot shape.
     * 
     * @param aspectFactor factor to apply t0 positions in mm to transform it to pixels.
     */
    @Override
    public void draw(double aspectFactor,int x0,int y0,VehicleState pos,Graphics2D g) {
    
        double D=150;   // Hopper - Round robot, diam of 150 mm
        
        this.aspectFactor=aspectFactor;
        
        AffineTransform at=g.getTransform();
        
        g.setStroke(new BasicStroke(1));
           
        // Rotate around the center of the robot
        g.rotate(Math.toRadians(pos.getAngle()), x0+mm2px(pos.getX()),y0-mm2px(pos.getY()));
        
        // Draw shapes around the center of the robot
        g.setColor(Color.CYAN);
        g.fillOval(x0+mm2px(pos.getX()-D/2),y0-mm2px(pos.getY()+D/2),mm2px(D),mm2px(D)); 
        g.setColor(Color.BLACK);
        g.drawOval(x0+mm2px(pos.getX()-D/2),y0-mm2px(pos.getY()+D/2),mm2px(D),mm2px(D)); 
        
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(x0+mm2px(pos.getX()-50),y0-mm2px(pos.getY()-10),mm2px(40),mm2px(40)); 
        g.fillOval(x0+mm2px(pos.getX()-50),y0-mm2px(pos.getY()+50),mm2px(40),mm2px(40)); 
        g.fillOval(x0+mm2px(pos.getX()+10),y0-mm2px(pos.getY()-10),mm2px(40),mm2px(40)); 
        g.fillOval(x0+mm2px(pos.getX()+10),y0-mm2px(pos.getY()+50),mm2px(40),mm2px(40)); 
        g.setColor(Color.BLACK);
        g.drawOval(x0+mm2px(pos.getX()-50),y0-mm2px(pos.getY()-10),mm2px(40),mm2px(40)); 
        g.drawOval(x0+mm2px(pos.getX()-50),y0-mm2px(pos.getY()+50),mm2px(40),mm2px(40)); 
        g.drawOval(x0+mm2px(pos.getX()+10),y0-mm2px(pos.getY()-10),mm2px(40),mm2px(40)); 
        g.drawOval(x0+mm2px(pos.getX()+10),y0-mm2px(pos.getY()+50),mm2px(40),mm2px(40)); 
        
        g.setTransform(at);
        
        // Draw sending signal state
        if (pos.getSignalState()>0) {
            double diam=D+pos.getSignalState()*30;
            g.setColor(Color.ORANGE);
            g.setStroke(new BasicStroke(2));
            g.drawOval(x0+mm2px(pos.getX()-diam/2),y0-mm2px(pos.getY()+diam/2),mm2px(diam),mm2px(diam)); 
        }
    }
    
}
