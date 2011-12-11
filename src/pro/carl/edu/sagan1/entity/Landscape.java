package pro.carl.edu.sagan1.entity;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static pro.carl.edu.sagan1.logic.MasterMind.log;

/**
 * Containes all information about one single mission landscape.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */   
public class Landscape {
    
    private String key;
    private int width;
    private int height;
    private VehicleState startPos;
    private BufferedImage backgroundImage;
    private String backgroundImagePath;
    private String backgroundImageCredit;
    private List<Form> forms=new ArrayList<Form>();
    
    
    /**
     * Initialising constructor.
     */
    public Landscape(String key) {
        this.key=key;
    }
    
    
    /**
     * Sets a set of properties.
     */
    public void setHeightWidthStartPosition(String height,String width,String startPosDescriptor) {
        
        this.height=Integer.parseInt(height);
        this.width=Integer.parseInt(width);
        
        int p1=startPosDescriptor.indexOf(',');
        int p2=startPosDescriptor.indexOf(',',p1+1);
        
        VehicleState p=new VehicleState(Integer.parseInt(startPosDescriptor.substring(0,p1)),
                                              Integer.parseInt(startPosDescriptor.substring(p1+1,p2)));
        switch(startPosDescriptor.charAt(p2+1)) {
            case 'l': case 'L': p.setAngle(270); break;
            case 'r': case 'R': p.setAngle(90); break;
            case 'u': case 'U': p.setAngle(0); break;
            case 'd': case 'D': p.setAngle(180); break;
            default: throw new IllegalArgumentException("Landscape start position, incorrect value:"+startPosDescriptor.charAt(p2));
        }
        
        this.startPos=p;
    }
    
    
    /**
     * Adds a shape object to the landscape.
     */
    public void addForm(Form shape) {
        getForms().add(shape);
    }
    
    
    /**
     * Adds a shape object by its descriptor string to the landscape.
     */
    public void addShape(String shapeDescriptor) {
        
        if (shapeDescriptor.isEmpty())
            return;
        
        int p1=shapeDescriptor.indexOf(',');
        int p2=shapeDescriptor.indexOf(',',p1+1);
        int p3=shapeDescriptor.indexOf(',',p2+1);
        int p4=shapeDescriptor.indexOf(',',p3+1);
        
        switch(shapeDescriptor.charAt(0)) {
            case 'R': case 'r': {
                Rectangle r=new Rectangle(Integer.parseInt(shapeDescriptor.substring(p1+1,p2).trim()),
                                          Integer.parseInt(shapeDescriptor.substring(p2+1,p3).trim()),
                                          Integer.parseInt(shapeDescriptor.substring(p3+1,p4).trim()),
                                          Integer.parseInt(shapeDescriptor.substring(p4+1).trim()));
                addForm(r);
                break;
            }
            case 'C': case 'c': {
                Circle c=new Circle(Integer.parseInt(shapeDescriptor.substring(p1+1,p2).trim()),
                                          Integer.parseInt(shapeDescriptor.substring(p2+1,p3).trim()),
                                          Integer.parseInt(shapeDescriptor.substring(p3+1).trim()));
                addForm(c);
                break;
            }
            case 'M': case 'm': {
                Mark m=new Mark(Integer.parseInt(shapeDescriptor.substring(p1+1,p2).trim()),
                                Integer.parseInt(shapeDescriptor.substring(p2+1,p3).trim()),
                                Integer.parseInt(shapeDescriptor.substring(p3+1,p4).trim()),
                                shapeDescriptor.substring(p4+1));
                addForm(m);
                break;
            }
            default: throw new IllegalArgumentException("Landscape, shape type incorrect:"+shapeDescriptor);    
        }
    }
    
    
    /**
     * Sets the background image and loads the bitmap into memory.
     */
    public void setBackgroundImagePath(String backgroundImagePath,String imageCredit) {
        
        this.backgroundImagePath=backgroundImagePath;
        this.backgroundImageCredit=imageCredit;
        
        try {                
            backgroundImage=ImageIO.read(new File(backgroundImagePath));
        } 
        catch (IOException ex) {
            System.out.println("Unable to load background image with key: "+key+", continue without! Error:"+ex.getMessage());
            log("Unable to load background image with key: "+key+", continue without! Error:"+ex.getMessage());
            backgroundImage=null;
        }
    }
    
    
    public int getHeight() {
        return height;
    }

    public String getKey() {
        return key;
    }

    public List<Form> getForms() {
        return forms;
    }

    public VehicleState getStartPosition() {
        return startPos;
    }

    public int getWidth() {
        return width;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }
   
    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public String getBackgroundImageCredit() {
        return backgroundImageCredit;
    }
 
}
