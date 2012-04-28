package pro.carl.edu.sagan1.logic;

import java.awt.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import pro.carl.edu.sagan1.entity.Landscape;
import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.gui.i18n.I18N;

import static pro.carl.edu.sagan1.logic.MasterMind.log;

/**
 * Singletone configuration container.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class Configuration {
    
    /** Singletone instance of the configuration manager. */
    private static Configuration configurationManager=new Configuration();
 
    /** General configuration properties map. Contains all properties from all configuration files. */
    private Properties configProps=null;
    
    private Map<String,Landscape> landscapes;
    private Map<String,Robot> robots;
    private List<Language> languages;
    
    /** Application icon, accessible for all dialoges */
    private Image applicationIcon;
    
    private int animationTimerFrequency=100;
    private int rp6RotationSpeed=50;
    private int rp6LineMoveSpeed=50;
    private int nxtLineMoveSpeed=40;
    private int nxtCalibrationDistance=1000;
    private int nxtCalibrationAngle=360;
    
    private boolean showMouseCoordinates=false;
        
    /**
     * Inner class defining a single Language record as displayed to the user for 
     * selection. Used in case of multi language descriptions for entities.
     */
    public static class Language {
        
        private String code;
        private String label;
        
        Language(String code,String label) {
            this.code=code;
            this.label=label;
        }
        
        @Override public String toString() { return label; }
        public String getCode() { return code; }
        public String getLabel() { return label; }
    }
    
    
    /**
     * Private constructor.
     */
    private Configuration() {
    }
    
    
    /**
     * Returns the single instance of the manager.
     */
    static public Configuration getInstance() {
        return configurationManager;
    }
    
    
    /**
     * Loads (or reloads) the configuration properties files and parses them into 
     * the configuration objects.
     */
    public void loadConfiguration() throws IOException {
       
        Properties previousConfigProp=this.configProps;
        Properties configProp=loadAllPropertiesFiles(); 
        
        this.configProps=configProp; // no IOException, replace config.

        try {
            parseConfig();
            parseRobots();
            parseLanguages();
            parseLandscapes();
        }
        catch(RuntimeException e) {
            if (previousConfigProp==null)
                throw e;
            else {
                log("(Re)parsing of configuration failed. Try to recover.");
                this.configProps=previousConfigProp; // reset to old config
                parseConfig();
                parseRobots();
                parseLanguages();
                parseLandscapes();
                throw e;
            }
        }
        log("(Re)loading configuration successful.");
    }

    
    /**
     * Loads all properties files into one single property map.
     */
    private Properties loadAllPropertiesFiles() throws IOException {
     
        Properties allConfigProp=new Properties();
        
        Properties prop=new Properties();
        log("(Re)loading configuration: config.properties");
        System.out.println("(Re)loading configuration: config.properties");
        System.out.flush();
        prop.load(new FileInputStream("config.properties"));
        allConfigProp.putAll(prop);

        prop=new Properties();
        log("(Re)loading configuration: missions.properties");
        System.out.println("(Re)loading configuration: missions.properties");
        System.out.flush();
        prop.load(new FileInputStream("missions.properties"));
        allConfigProp.putAll(prop);
        
        prop=new Properties();
        log("(Re)loading configuration: robots.properties");
        System.out.println("(Re)loading configuration: robots.properties");
        System.out.flush();
        prop.load(new FileInputStream("robots.properties"));
        allConfigProp.putAll(prop);
        
        prop=new Properties();
        log("(Re)loading configuration: translations.properties");
        System.out.println("(Re)loading configuration: translations.properties");
        System.out.flush();
        prop.load(new FileInputStream("translations.properties"));
        allConfigProp.putAll(prop);
        
        return allConfigProp;
    }
    
    
    /**
     * Parses the configuration properties and sets all application configuration values.
     */
    private void parseConfig() {
        
        animationTimerFrequency=Integer.parseInt(getProperty(ConfigProperties.SIM_ANIM_TICKER_SPEED));
        if (animationTimerFrequency<10 || animationTimerFrequency>2000) animationTimerFrequency=110;
        
        rp6RotationSpeed=Integer.parseInt(getProperty(ConfigProperties.RP6_EXEC_ROTATIONSPEED));
        if (rp6RotationSpeed<15 || rp6RotationSpeed>120) rp6RotationSpeed=50;
    
        rp6LineMoveSpeed=Integer.parseInt(getProperty(ConfigProperties.RP6_EXEC_LINEMOVESPEED));
        if (rp6LineMoveSpeed<15 || rp6LineMoveSpeed>120) rp6LineMoveSpeed=50;
        
        nxtLineMoveSpeed=Integer.parseInt(getProperty(ConfigProperties.ROBOTS_SUFFIX_NXT_SPEED));
        if (nxtLineMoveSpeed<10 || nxtLineMoveSpeed>100) nxtLineMoveSpeed=40;
              
        nxtCalibrationDistance=Integer.parseInt(getProperty(ConfigProperties.ROBOTS_SUFFIX_NXT_CAL_DIST));
        if (nxtCalibrationDistance<1) nxtCalibrationDistance=1;
        nxtCalibrationAngle=Integer.parseInt(getProperty(ConfigProperties.ROBOTS_SUFFIX_NXT_CAL_DEGREE));
        if (nxtCalibrationAngle<0) nxtCalibrationAngle=0;
        if (nxtCalibrationAngle>360) nxtCalibrationAngle=360;
                
        showMouseCoordinates=false;
        if ("TRUE".equalsIgnoreCase(getProperty(ConfigProperties.DISP_SHOWCURSORCOORDS)))
            showMouseCoordinates=true;
    }

    
    /**
     * Parses the configuration properties and retrieves the list of robots.
     */
    private void parseRobots() {
     
        String robs[]=getProperty(ConfigProperties.ROBOTS_LIST_PREFIXES).split(",");
        
        robots=new HashMap<String,Robot>();
    
        for (String rob:robs) {
            Map<String,String> data=retrieve(rob);
            
            Robot r=new Robot(rob);
            r.setName(data.get(ConfigProperties.ROBOTS_SUFFIX_SCREENNAME.toString()));
            r.setModelId(data.get(ConfigProperties.ROBOTS_SUFFIX_MODELID.toString()));
            r.setPictureFilePath(data.get(ConfigProperties.ROBOTS_SUFFIX_PICTURE.toString()));
            
            String rp6=data.get(ConfigProperties.ROBOTS_SUFFIX_RP6_ENC_RES.toString());
            r.setRp6CalibrationEncoderResolution((rp6==null || rp6.isEmpty()) ? 0.0 : Double.parseDouble(rp6));
            rp6=data.get(ConfigProperties.ROBOTS_SUFFIX_RP6_ROT_FACT.toString());
            r.setRp6CalibrationRotationFactor((rp6==null || rp6.isEmpty()) ? 0.0 : Double.parseDouble(rp6));

            String nxt=data.get(ConfigProperties.ROBOTS_SUFFIX_NXT_ROTATION_TIME.toString());
            r.setNxtCalibrationUnitRotationTime((nxt==null || nxt.isEmpty()) ? 0 : Integer.parseInt(nxt));
            nxt=data.get(ConfigProperties.ROBOTS_SUFFIX_NXT_LINE_TIME.toString());
            r.setNxtCalibrationUnitMovementTime((nxt==null || nxt.isEmpty()) ? 0 : Integer.parseInt(nxt));
            
            // (Config parsed before robots) 
            r.setNxtCalibrationTimePerDegree((double)r.getNxtCalibrationUnitRotationTime()/(double)getNxtCalibrationAngle());
            r.setNxtCalibrationTimePerMillimeter((double)r.getNxtCalibrationUnitMovementTime()/(double)getNxtCalibrationDistance());
            
            nxt=data.get(ConfigProperties.ROBOTS_SUFFIX_NXT_USES_COMPASS.toString());
            r.setNxtUsesCompassSensor((nxt==null || nxt.isEmpty()) ? false : Boolean.parseBoolean(nxt));
            
            // TODO: ADD  r.validate() true/false w/ error in log, add only valid robot entries, do same for other entities
            robots.put(rob,r);
        }
    }

    
    /**
     * Parses the configuration properties and retrieves the landscape parameters.
     */
    private void parseLandscapes() {
     
        String lands[]=getProperty(ConfigProperties.LANDS_LIST_PREFIXES).split(",");
        
        landscapes=new HashMap<String,Landscape>();
    
        for (String land:lands) {
            
            Map<String,String> data=retrieve(land);
            
            Landscape l=new Landscape(land); 
            landscapes.put(land,l);
            
            l.setHeightWidthStartPosition(data.get(ConfigProperties.LANDS_SUFFIX_HEIGHT.toString()),
                                          data.get(ConfigProperties.LANDS_SUFFIX_WIDTH.toString()),
                                          data.get(ConfigProperties.LANDS_SUFFIX_ROBOTSTARTPOS.toString()));
            
            l.setBackgroundImagePath(data.get(ConfigProperties.LANDS_SUFFIX_BACKGROUNDIMG.toString()),
                                     data.get(ConfigProperties.LANDS_SUFFIX_BACKGROUNDIMG_CREDITS.toString()));
            
            String shapes[]=data.get("shapes").split(";");
            for (String shape:shapes)
                l.addShape(shape.trim());
        }
    }

    
    /**
     * Returns a key/value pair map of properties starting with the passed key prefix. 
     * The prefix is removed from key before returning.
     */
    private Map<String,String> retrieve(String keyPrefix) {
        
        Map<String,String> result=new HashMap<String, String>();
        
        for (Entry<Object, Object> entry:configProps.entrySet()) {
            String key=(String)entry.getKey();
            if (key.startsWith(keyPrefix)) {
                String val=(String)entry.getValue();
                result.put(key.substring(keyPrefix.length()+1),val);
            }
        }
        return result;
    }
    
    
    /**
     * Parses the configuration properties and sets-up the I18N manager.
     */
    private void parseLanguages() {
     
        String[] langCodes=getProperty(ConfigProperties.LANGUAGES_LIST).split(",");
        String[] langLabels=getProperty(ConfigProperties.LANGUAGES_LABELS).split(",");

        languages=new ArrayList<Language>();
    
        for (int i=0;i<langCodes.length;i++) {
            Language l=new Language(langCodes[i],langLabels[i]);
            languages.add(l);
        }
        
        for (Language lang:languages) {
            I18N.getInstance().addLangBase(lang.getCode(),retrieve(lang.getCode()));
        }
        
        I18N.getInstance().setLangCode(I18N.DEFAULT_LANG);
    }

    
    /**
     * Returns the robot identified by the key.
     */
    public Robot getRobot(String key) {
        return robots.get(key);
    }
    
    
    /**
     * Writes the application environment properties to stdout.
     */
    public void outRuntimeInfo() {
        
        System.out.println("Detected Run-Time parameters:");
        for (Map.Entry<Object,Object> e:System.getProperties().entrySet()) {
            if (e.getKey().toString().startsWith("java") || e.getKey().toString().startsWith("os") || e.getKey().toString().startsWith("user"))
                System.out.println("*"+e.getKey().toString()+"="+e.getValue().toString());
        }
        System.out.println();
    }

    
    
    /**
     * 
     */
    public int sizeRobots() {
        return robots.size();
    }
    
    /**
     * 
     */
    public Collection<Robot> getAllRobots() {
        return robots.values();
    }

    /**
     * 
     */
    public Landscape getLandscape(String key) {
        return landscapes.get(key);
    }
    
    /**
     * 
     */
    public int sizeLandscapes() {
        return landscapes.size();
    }
    
    /**
     * 
     */
    public Collection<Landscape> getAllLandscapes() {
        return landscapes.values();
    }
    
    /**
     * 
     */
    public List<Language> getLanguages() {
        return languages;
    }
    
    /**
     * 
     */
    public void setAppIcon(Image applicationIcon) {
        this.applicationIcon=applicationIcon;
    }
    
    /**
     * 
     */
    public Image getAppIcon() {
        return this.applicationIcon;
    }
    
    /**
     * 
     */
    public int getAnimationTimerFrequency() {
        return animationTimerFrequency;
    }
    
    /**
     * General properties retrieval method.
     */
    public String getProperty(ConfigProperties prop) {
        return configProps.getProperty(prop.toString());
    }
    
    /**
     * 
     */
    public int getRp6RotationSpeed() {
        return rp6RotationSpeed;
    }
    
    /**
     * 
     */
    public int getRp6LineMoveSpeed() {
        return rp6LineMoveSpeed;
    }

    /**
     * 
     */
    public boolean showMouseCoordinates() {
        return showMouseCoordinates;
    }

    /**
     * 
     */
    public int getNxtLineMoveSpeed() {
        return nxtLineMoveSpeed;
    }

    /**
     * 
     */
    public int getNxtCalibrationAngle() {
        return nxtCalibrationAngle;
    }

    /**
     * 
     */
    public int getNxtCalibrationDistance() {
        return nxtCalibrationDistance;
    }
   
}
