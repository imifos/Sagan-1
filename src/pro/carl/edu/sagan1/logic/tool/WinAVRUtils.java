package pro.carl.edu.sagan1.logic.tool;

import static pro.carl.edu.sagan1.logic.MasterMind.log;
import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;


/**
 * Contains WinAVR related operations. 
 * <p/>
 * WinAVR(tm) (pronounced "whenever") is a suite of executable, open source software development tools for the Atmel AVR 
 * series of RISC microprocessors hosted on the Windows platform. It includes the GNU GCC compiler for C and C++.
 * <p/>
 * WinAVR(tm) contains all the tools for developing on the AVR. This includes avr-gcc (compiler), avrdude (programmer), 
 * avr-gdb (debugger), and more! WinAVR is used all over the world from hobbyists sitting in their damp basements, 
 * to schools, to commercial projects.
 * <p/>
 * WinAVR(tm) is comprised of many open source projects. If you feel adventurous, volunteers are always welcome to help 
 * with fixing bugs, adding features, porting, writing documentation and many other tasks on a variety of projects. 
 * 
 * @link http://winavr.sourceforge.net/
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class WinAVRUtils {
    
    static private Boolean isInstalled=null ; // init to 'not tested'
    
    /**
     * Performs the verification if the WinAVR tool suite is installed and
     * writes the result in the log file. This has to be performed once only.
     * Afterwards simply use "isInstalled()".
     */
    static public boolean lookFor() {
        
        // File.exists() doesn't search within the system file path.
        // Need to find a solution later.
        
        /**
        isInstalled=Boolean.TRUE;
        
        // Test if all files we need for the RP6 compilation with WinAVR
        // are in the file path.
        if ( !(new File("avr-gcc")).exists()) {
            if (isInstalled.booleanValue()) log("WinAVR installation verification:");
            log("    Compiler 'avr-gcc' not found!");
            isInstalled=Boolean.FALSE;
        }
        if ( !(new File("avr-objcopy")).exists()) {
            if (isInstalled.booleanValue()) log("WinAVR installation verification:");
            log("    Tool 'avr-objcopy' not found!");
            isInstalled=Boolean.FALSE;
        }
        if ( !(new File("avr-objdump")).exists()) {
            if (isInstalled.booleanValue()) log("WinAVR installation verification:");
            log("    Tool 'avr-objdump' not found!");
            isInstalled=Boolean.FALSE;
        }
        if ( !(new File("avr-nm")).exists()) {
            if (isInstalled.booleanValue()) log("WinAVR installation verification:");
            log("    Tool 'avr-nm' not found!");
            isInstalled=Boolean.FALSE;
        }
        */
        return false;
    }
    
    /**
     * Returns TRUE is the WinAVR package is installed, other wise returns
     * FALSE.
     */
    static public boolean isInstalled() {
        if (isInstalled==null)
            WinAVRUtils.lookFor();
        return false;
    }
    
}
