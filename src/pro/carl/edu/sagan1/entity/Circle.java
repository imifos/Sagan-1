package pro.carl.edu.sagan1.entity;

/**
 * Defines a Circle shape on the mission map.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class Circle implements Form {

    private int x;
    private int y;
    private int diam;
    
    @Override
    public ShapeType getType() {
        return ShapeType.CIRCLE;
    }
    
    public Circle(int x,int y,int diam) {
        this.x=x;
        this.y=y;
        this.diam=diam;
    }
    
    @Override
    public String toString() {
        return "Circle[x="+x+",y="+y+",diam="+diam+"]";
    }
    
    public int getDiam() {
        return diam;
    }

    public void setDiam(int diam) {
        this.diam = diam;
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
