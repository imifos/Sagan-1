package pro.carl.edu.sagan1.gui.i18n;

import java.util.Map;
import java.util.HashMap;

/**
 * Container for all screen texts in all languages. Initialised during configuration
 * loading and later generally staticly imported for easy access.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class I18N {
    
    public static String DEFAULT_LANG="en";
    
    /** Singletone instance */
    private static I18N i18n=new I18N();

    /** Map of <text ID, text> */
    private Map<String,String> currentCache=new HashMap<String, String>();
    
    /** Map of <language code, Map of <text ID, text>> */
    private Map<String,Map<String,String>> allCaches=new HashMap<String,Map<String, String>>();
    
    /** Currently active language. */
    private String currentLangCode="en";
    
    /**
     * Private constructor.
     */
    private I18N() {
    }
    
    /**
     * Returns the singletone instance of the I18N container.
     */
    static public I18N getInstance() {
        return i18n;
    }
    
    
    /**
     * Staticly imported method for easy access to translations.
     */
    static public String i18n(String key) {
        String t=i18n.currentCache.get(key);
        return t==null ? key:t;
    }
        
    
    /**
     * Adds a new set of strings loaded from config into the language cache.
     */
    public void addLangBase(String langCode,Map<String,String> langKeyValueBase) {
        allCaches.put(langCode,langKeyValueBase);
    }
    
    /**
     * Activates a new language.
     */
    public void setLangCode(String langCode) {
        currentCache=allCaches.get(langCode);
        currentLangCode=langCode;
    }
    
    /**
     * Returns the currently active language.
     */
    public String getLangCode() {
        return currentLangCode;
    }
}
