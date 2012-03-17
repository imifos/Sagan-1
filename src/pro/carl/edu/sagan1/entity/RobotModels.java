package pro.carl.edu.sagan1.entity;

/**
 * Enumerates all supported robot model and types.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public enum RobotModels {
    
    RP6             ("rp6"),
    HOPPER          ("hopper"),
    LEGO_ROVER1     ("lego1"),
    ;
    
    private String modelKey;
    
    private RobotModels(String modelKey) {
        this.modelKey=modelKey;
    }
    
    @Override
    public String toString() {
        return getModelKey();
    }
    
    public String getModelKey() {
        return modelKey;
    }
}
