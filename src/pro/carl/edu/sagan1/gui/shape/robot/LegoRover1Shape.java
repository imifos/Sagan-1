package pro.carl.edu.sagan1.gui.shape.robot;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import pro.carl.edu.sagan1.entity.RobotModels;
import pro.carl.edu.sagan1.entity.VehicleState;

/**
 * Draws the LEGO Rover Versio 1 robot shape into the passed graphic context.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class LegoRover1Shape implements RobotShape {
   
    /** ID of this robot shape covered by this class. */
    static public final String MODELID=RobotModels.LEGO_ROVER1.getModelKey();
    
    /** Lego Orange :) */
    static final private Color MYORANGE=new Color(244, 143, 40);
    
    /** NXT font, create once - use anytime. */
    private Font nxtFont=new Font("Sans Serif",Font.PLAIN,9);
    
    
    /** Globalised "aspectFactor". @see pro.carl.edu.sagan1.gui.viewcon.SimulationJPanel */
    private double aspectFactor=0;
    
    /**
     * Returns a new instance of this robot shape.
     */
    static public LegoRover1Shape getInstance() {
        return new LegoRover1Shape();
    }
    
    /**
     * Package-private constructor to force usage of factory method.
     */
    LegoRover1Shape() {
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
    
        double W=130;   // Lego robot of 130 mm width
        double H=150;   // and 150 mm height, without cables and without head sensor
        
        this.aspectFactor=aspectFactor;
        
        AffineTransform at=g.getTransform();
        
        // Rotate around the center of the robot
        g.rotate(Math.toRadians(pos.getAngle()), x0+mm2px(pos.getX()),y0-mm2px(pos.getY()));
        
        // Draw shapes around the center of the robot
        // Rectangle
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x0+mm2px(pos.getX()-W/2)+10,y0-mm2px(pos.getY()+H/2),mm2px(W)-20,mm2px(H)); 
        g.setColor(Color.BLACK);
        g.drawRect(x0+mm2px(pos.getX()-W/2)+10,y0-mm2px(pos.getY()+H/2),mm2px(W)-20,mm2px(H));                
        
        // Brick
        g.setColor(Color.BLACK);
        g.drawRect(x0+mm2px(pos.getX()-W/2)+20,y0-mm2px(pos.getY()+30),mm2px(W)-40,mm2px(H)-30); // outer
        g.setColor(MYORANGE);
        g.fillRect(x0+mm2px(pos.getX()-W/2)+25,y0-mm2px(pos.getY()+5),mm2px(W)-50,mm2px(H)-50); // inner       
        g.setColor(Color.BLACK);
        g.drawRect(x0+mm2px(pos.getX()-W/2)+25,y0-mm2px(pos.getY()+5),mm2px(W)-50,mm2px(H)-50); // inner       
        
        g.setColor(Color.BLACK);
        g.setFont(nxtFont);
        g.drawString("NXT",x0+mm2px(pos.getX()-W/2-5)+25,y0-mm2px(pos.getY()+10));
        
        // Sensor Head
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x0+mm2px(pos.getX()-10),y0-mm2px(pos.getY()+H/2+60),mm2px(20),mm2px(90));    
        g.setColor(Color.BLACK);
        g.drawRect(x0+mm2px(pos.getX()-10),y0-mm2px(pos.getY()+H/2+60),mm2px(20),mm2px(90));    
        g.setColor(MYORANGE);
        g.fillRect(x0+mm2px(pos.getX()-20),y0-mm2px(pos.getY()+H/2+60),mm2px(40),mm2px(20));    
        g.setColor(Color.BLACK);
        g.drawRect(x0+mm2px(pos.getX()-20),y0-mm2px(pos.getY()+H/2+60),mm2px(40),mm2px(20));    
                
        // Both chains
        g.setColor(Color.BLACK);
        g.fillOval(x0+mm2px(pos.getX()-W/2),
                   y0-mm2px(pos.getY()+H/2),
                   mm2px(30),
                   mm2px(H));
        g.fillOval(x0+mm2px(pos.getX()-W/2+(W-30)),
                   y0-mm2px(pos.getY()+H/2),
                   mm2px(30),
                   mm2px(H));
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(x0+mm2px(pos.getX()-W/2),
                   y0-mm2px(pos.getY()+H/2),
                   mm2px(30),
                   mm2px(H));
        g.drawOval(x0+mm2px(pos.getX()-W/2+(W-30)),
                   y0-mm2px(pos.getY()+H/2),
                   mm2px(30),
                   mm2px(H));
        
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
