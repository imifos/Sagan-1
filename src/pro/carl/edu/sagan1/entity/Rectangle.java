package pro.carl.edu.sagan1.entity;

/**
 * Defines a rectangle shape on the mission's landmark.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class Rectangle implements Form {

    private int x;
    private int y;
    private int w;
    private int h;
    
    @Override
    public ShapeType getType() {
        return ShapeType.RECTANGLE;
    }
    
    public Rectangle(int x,int y,int w,int h) {
        this.h=h;
        this.w=w;
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString() {
        return "Rectangle[x="+x+",y="+y+",w="+w+",h="+h+"]";
    }
    
    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
 
}
