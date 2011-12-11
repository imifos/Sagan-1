package pro.carl.edu.sagan1.entity;

/**
 * Defines a landmark on the missions landscape.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class Mark implements Form {

    private int x;
    private int y;
    private int angle;
    private String text;

    public Mark(int x,int y,int angle,String text) {
        this.x=x;
        this.y=y;
        this.angle=angle;
        this.text=text;
    }
    
    @Override
    public ShapeType getType() {
        return ShapeType.TASKMARK;
    }
    
    public int getAngle() {
        return angle;
    }
    
    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
}
