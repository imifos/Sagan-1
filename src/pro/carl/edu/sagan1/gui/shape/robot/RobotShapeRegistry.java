package pro.carl.edu.sagan1.gui.shape.robot;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry that manages all robot shapes currentlu know by the application and 
 * allows to obtain the drawer class via the robot ID.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class RobotShapeRegistry {

    static public RobotShape DEFAULT_SHAPE=SimpleRobotShape.getInstance();

    /** The registry that maps model ID to drawer object. */
    static private Map<String,RobotShape> robots=null;
    
    /**
     * Initialises the registry once.
     */
    static private void init() {
        
        if (robots==null) {
            robots=new HashMap<String, RobotShape>();
        
            robots.put(SimpleRobotShape.MODELID,SimpleRobotShape.getInstance());
            robots.put(RP6RobotShape.MODELID,RP6RobotShape.getInstance());
            robots.put(HopperRobotShape.MODELID,HopperRobotShape.getInstance());
        }
    }

    /**
     * 
     */
    static public RobotShape getShape(String modelId) {
        
        RobotShapeRegistry.init();
                
        RobotShape r=RobotShapeRegistry.robots.get(modelId);
        if (r==null) r=DEFAULT_SHAPE;
        return r;
    }
}
