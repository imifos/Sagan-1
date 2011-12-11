package pro.carl.edu.sagan1.logic;

/**
 * Enumerations all configuration properties. Key documentation in propertiy files.
 * 
 * @version 1.0.0
 * @since 0.0
 */
public enum ConfigProperties {
    
    // Application
    
    DIR_USERPROGRAMS                    ("config.dir.userprograms"),
    
    // Translation / I18N
    
    LANGUAGES_LIST                      ("lang.list"),
    LANGUAGES_LABELS                    ("lang.label"),
    
    // Landscapes and mission regions
    
    /** List of landscapes that defines at the same time the keys prefixes for
     the suffixes defined hereafter. Final property key: Prefix + Suffix. */
    LANDS_LIST_PREFIXES                 ("land.list"), 
    //
    LANDS_SUFFIX_HEIGHT                 ("height"),
    LANDS_SUFFIX_WIDTH                  ("width"),
    LANDS_SUFFIX_ROBOTSTARTPOS          ("startpos"),
    LANDS_SUFFIX_BACKGROUNDIMG          ("backgroundimage"),
    LANDS_SUFFIX_BACKGROUNDIMG_CREDITS  ("backgroundimage.credit"),
    LANDS_SUFFIX_SHAPES                 ("shapes"),
    
    // Robots
    
    /** List of Robots that defines at the same time the keys prefixes for
    the suffixes defined hereafter. Final property key: Prefix + Suffix. */
    ROBOTS_LIST_PREFIXES                ("robot.list"),
    //
    ROBOTS_SUFFIX_SCREENNAME            ("name"),
    ROBOTS_SUFFIX_MODELID               ("modelid"),
    ROBOTS_SUFFIX_PICTURE               ("picture"),
    ROBOTS_SUFFIX_RP6_ENC_RES           ("rp6.calibration.encoder_resolution"),
    ROBOTS_SUFFIX_RP6_ROT_FACT          ("rp6.calibration.rotation_factor"),
    
    // Display
    
    DISP_SHOWCURSORCOORDS               ("config.display.show.mouse.coordinates"),
            
    // Simulation
    
    SIM_ANIM_TICKER_SPEED               ("config.simulation.ticker.speed"),
    
    // RP6
    
    RP6_LOADEREXE                       ("config.rp6.exe.loader"),
    RP6_RP6CONFIG_H                     ("config.rp6.rp6config_h"),
    RP6_DIR_COMPILING                   ("config.rp6.dir.compiling"),
    RP6_DIR_HEXDEST                     ("config.rp6.dir.hexout"),
    RP6_EXEC_ROTATIONSPEED              ("config.rp6.execution.rotation.speed"),
    RP6_EXEC_LINEMOVESPEED              ("config.rp6.execution.linemove.speed"),
    
    
        
    $("");
           
    /** Key name in the property file. */
    private String key;
    
    /**
     * 
     */
    private ConfigProperties(String key) {
        this.key=key;
    }

    
    /**
     * Returns the property file key string.
     */
    @Override
    public String toString() {
        return key;
    }
}
