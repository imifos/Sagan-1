package pro.carl.edu.sagan1.gui.shape.robot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import pro.carl.edu.sagan1.entity.VehicleState;

/**
 * Draws a simple base robot shape into the passed graphic context.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class SimpleRobotShape implements RobotShape {
    
    /** ID of this robot shape covered by this class. */
    static public final String MODELID="simple";
    
    /** Globalised "aspectFactor". @see pro.carl.edu.sagan1.gui.viewcon.SimulationJPanel */
    private double aspectFactor=0;
    
    /**
     * Returns a new instance of this robot shape.
     */
    static public SimpleRobotShape getInstance() {
        return new SimpleRobotShape();
    }
    
    
    /**
     * Package-private constructor to force usage of factory method.
     */
    SimpleRobotShape() {
    }
    
    
    /**
     * Converts the passed mm value to pixel using the pre-calculated aspect factor.
     * 
     * @see SimulationJPanel#computeDisplayParameters(java.awt.Graphics2D) 
     */
    private int mm2px(double mm) {
        return (int)(mm*aspectFactor);
    }

    
    /**
     * Draws the robot shape.
     * @param aspectFactor factor to apply t0 positions in mm to transform it to pixels.
     */
    @Override
    public void draw(double aspectFactor,int x0,int y0,VehicleState pos,Graphics2D g) {
    
        double W=110;   // RP6 robot of 110 mm width
        double H=180;   // and 180 mm height
        
        this.aspectFactor=aspectFactor;
        
        AffineTransform at=g.getTransform();
        
        // Rotate around the center of the robot
        g.rotate(Math.toRadians(pos.getAngle()), x0+mm2px(pos.getX()),y0-mm2px(pos.getY()));
        
        // Draw shapes around the center of the robot
        // Rectangle
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.PINK);
        g.fillRect(x0+mm2px(pos.getX()-W/2),y0-mm2px(pos.getY()+H/2),mm2px(W),mm2px(H)); 
        g.setColor(Color.BLACK);
        g.drawRect(x0+mm2px(pos.getX()-W/2),y0-mm2px(pos.getY()+H/2),mm2px(W),mm2px(H));                
        
        g.drawLine(x0+mm2px(pos.getX()-W/2),y0-mm2px(pos.getY()+H/2-20),
                   x0+mm2px(pos.getX()-W/2+W),y0-mm2px(pos.getY()+H/2-20));
        
        // Both chains
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(x0+mm2px(pos.getX()-W/2),
                   y0-mm2px(pos.getY()+H/2-20/2),
                   mm2px(30),
                   mm2px(H-20));
        g.fillOval(x0+mm2px(pos.getX()-W/2+(W-30)),
                   y0-mm2px(pos.getY()+H/2-20/2),
                   mm2px(30),
                   mm2px(H-20));
        g.setColor(Color.BLACK);
        g.drawOval(x0+mm2px(pos.getX()-W/2),
                   y0-mm2px(pos.getY()+H/2-20/2),
                   mm2px(30),
                   mm2px(H-20));
        g.drawOval(x0+mm2px(pos.getX()-W/2+(W-30)),
                   y0-mm2px(pos.getY()+H/2-20/2),
                   mm2px(30),
                   mm2px(H-20));
        
        g.setTransform(at);
        
        // Draw sending signal state
        if (pos.getSignalState()>0) {
            double diam=W+pos.getSignalState()*30;
            g.setColor(Color.ORANGE);
            g.setStroke(new BasicStroke(2));
            g.drawOval(x0+mm2px(pos.getX()-diam/2),y0-mm2px(pos.getY()+diam/2),mm2px(diam),mm2px(diam)); 
        }
    }
    
}
