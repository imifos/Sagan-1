package pro.carl.edu.sagan1.gui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * JPanel holding a sizeable image.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class ImageJPanel extends JPanel {

    private Image image;
    
    /**
     * 
     */
    public ImageJPanel() {
        super();
        setLayout(null);
    }
    
    
    /**
     * Set or resets the image.
     */
    public void setImage(Image image) {
        this.image=image;
        if (image!=null) {
            Dimension d=new Dimension(image.getWidth(null),image.getHeight(null));
            setSize(d);
            setPreferredSize(d);
        } 
    }
    
    /**
     * Draws the component.
     */
    @Override
    public void paintComponent(Graphics g) {

        if (image==null)
            return;
        
        int w=getWidth();
        if (w>250) w=250;
        if (w<20) w=20;
        
        Graphics2D g2=(Graphics2D)g;
        
        Image i=image.getScaledInstance(w,-1,Image.SCALE_SMOOTH);
        int x=getWidth()/2-i.getWidth(null)/2;
        g2.drawImage(i,x,0,null);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x,1,w-2,i.getHeight(null)-2);
    }
}
