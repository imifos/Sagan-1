package pro.carl.edu.sagan1.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import pro.carl.edu.sagan1.entity.Landscape;
import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.entity.VehicleState;
import pro.carl.edu.sagan1.gui.i18n.I18N;
import pro.carl.edu.sagan1.logic.scripting.ProgramExecutor;
import pro.carl.edu.sagan1.logic.compiling.CompilationRunningException;
import pro.carl.edu.sagan1.logic.scripting.SimulationRunningException;
import pro.carl.edu.sagan1.logic.compiling.ProgramCompiler;

import static pro.carl.edu.sagan1.logic.MasterMind.log;
import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;
import pro.carl.edu.sagan1.logic.compiling.ProgramCompilerFactory;



/**
 * The central application gear box. Manages communication between presentation and 
 * logic. 
 * 
 * @since 0.0
 * @version 1.1.0 
 */
public class MasterMind implements ActionListener {
        
    // Event IDs
    static public final int EVENTID_LANDSCAPECHANGED=32319342;
    static public final int EVENTID_ROBOTCHANGED=867321967;
    static public final int EVENTID_LOGMESSAGENOTIFICATION=123167;
    static public final int EVENTID_GUILANGCHANGENOTIFICATION=3185172;
    
    static public final int EVENTID_SIMULATION_STARTED=673251;
    static public final int EVENTID_SIMULATION_VEHICULEMOVED=71527645;
    static public final int EVENTID_SIMULATION_FINISHED=3187263;
    static public final int EVENTID_SIMULATION_NEWCOMMAND=3571;
    static public final int EVENTID_SIMULATION_EXECUTIONERROR=831867;
    static public final int EVENTID_SIMULATION_ABORTED=8318712;
    
    static public final int EVENTID_COMPILATION_STARTED=781123852;
    static public final int EVENTID_COMPILATION_FAILED=781123853;
    static public final int EVENTID_COMPILATION_SUCCEEDED=781123854;
    static public final int EVENTID_COMPILATION_PARSINGERROR=781123855;
    static public final int EVENTID_COMPILATION_OUTPUT=781123856;
    static public final int EVENTID_COMPILATION_NOCOMPILER=781211283;
    
    
    /** Singletone instance of the state manager. */
    private static MasterMind instance=new MasterMind();
    
    /** List of GUI update event listeners registered against the StateManager. */
    private List<ActionListener> displayUpdateListeners=new ArrayList<ActionListener>();
    /** List of observers that want to be informed about events logging. */
    private List<ActionListener> logMessageNotificationListeners=new ArrayList<ActionListener>();
    /** List of observers that want to be informed about GUI language changes. */
    private List<ActionListener> languageChangeNotificationListeners=new ArrayList<ActionListener>();
    /** List of observers that want to be informed about compilation, packagebuilding and communication process. */
    private List<ActionListener> compilationNotificationListeners=new ArrayList<ActionListener>();
    
    /** Currently active robot. */
    private Robot currentRobot;
    
    /** Currently active landscape. */
    private Landscape currentLandscape;
    
    /** Current position of the vehicule, kept at the central place since 
     modifier objects are coming and going. */
    private VehicleState currentVehiclePosition;
    
    /** The programm executor instantiated during active simulation. */
    private ProgramExecutor executor;
    
    /** The programm executor instantiated during active compilation and communication. */
    private ProgramCompiler compiler;
    
    /** Keeps the fact that this instance has been initialised once. */
    private boolean isStartedUp=false;
    
    /** External procress when RP6Loader is started from this applic. */
    private Process processRP6Loader=null;
    
    
    
    /**
     * Private constructor.
     */
    private MasterMind() {
    }
    
    
    /**
     * Returns the single instance of the manager.
     */
    static public MasterMind getInstance() {
        return instance;
    }

    
    /**
     * Static method that outputs an empty line into the system log panel.
     * Available for static import.
     */
    static public void log() {
        instance.writeLog("");
    }

    
    /**
     * Static method that outputs a message into the system log panel.
     * Available for static import.
     */
    static public void log(String msgKey) {
        instance.writeLog(i18n(msgKey));
    }
    
    
    /**
     * Static method that outputs a message into the system log panel.
     * Available for static import.
     */
    static public void log(String msgKey,String msg) {
        instance.writeLog(i18n(msgKey)+" "+msg);
    }
    
    
    /**
     * Static method that outputs a message into the system log panel.
     * Available for static import.
     */
    static public void log(String msgKey,Exception e) {
        instance.writeLog(i18n(msgKey),e);
    }
    
    
    /**
     * Initialises the master controler at program start-up, right after loading
     * the configuration files BUT BEFORE GUI SET-UP.
     */
    public void onStartUpHeadless() {
        
        if (isStartedUp)
            throw new IllegalStateException("Cannot start-up the master controller twice!");
        isStartedUp=true;
        
        // Headless (!) initialisation work goes here...
    }
    
    
    /**
     * Initialises the master controler at program start-up, right after initialising
     * the GUI.
     */
    public void onStartUpWithGuiUp() {

        log("log.appstartup",Versions.APP_TITLE+" - "+Versions.APP_VERSION);
        log("-------------------------------------------------------------------------------");
        log("");
        log("Started in: "+System.getProperty("user.dir"));
        
        // Check if Win-AVG is installed
        // if (!WinAVRUtils.isInstalled())  -- see class
        //    log("You can use the simulator, but you will not be able to compile for the RP6 Robot model!");
    }
    
    
    /**
     * Called just before the application closes and exits.
     */
    public void onCloseApplication() {
        
        // If we have an external RP6Loader running, detach at exit!
        if (processRP6Loader!=null) {
            try {
                System.out.println("Over and Out!");
                processRP6Loader.destroy();
            }
            catch(Exception e) {
                System.out.println("Error while closing RP6Loader process:"+e.getMessage());
            }
        }
    }

    
    /**
     * Starts a new simulation. Initialises simulator and executor, registers
     * this class as observer to the executor and starts the execution.
     * As of this moment, the simulation will run in the background and this
     * class get informed on an event basis.
     */
    public void startSimulation(List<String> commandList) throws SimulationRunningException {
    
        if (executor!=null)
            throw new SimulationRunningException();
        
        currentVehiclePosition=getCurrentLandscape().getStartPosition();
        
        executor=new ProgramExecutor(commandList,currentLandscape.getStartPosition());
        executor.addEventListener(this);
        executor.start();
    }
    
    
    /**
     * Commands the controller to stop the current simulation, if there is any.
     */
    public void stopSimulation() {
        if (executor!=null) {
            executor.stop();
        }
    }
    
    
    /**
     * Asks to place the vehicule at the start position. Works only if there is 
     * no simulation ongoing.
     */
    public void resetVehiculeToStartPosition() {
        if (executor==null) {
            // We aren't inside a simulation but the display comonents shoud behave like it's the case.
            currentVehiclePosition=getCurrentLandscape().getStartPosition();
            fireDisplayUpdate(new ActionEvent(this,MasterMind.EVENTID_SIMULATION_VEHICULEMOVED,null));
        }
    }
    
    
    /**
     * The cross-compilation has been started and this method effectively starts 
     * the procedure.
     */
    public void buildBinaryPackage(List<String> commandList) throws CompilationRunningException {
        
        if (compiler!=null) // Should be avoided by GUI
            throw new CompilationRunningException();
        
        // Get the compiler for the selected robot
        compiler=ProgramCompilerFactory.getCompiler(getCurrentRobot());
        if (compiler==null) {
            // No adequate compiler available
            log("No compiler for currently selected robot available. Sorry! (ID="+getCurrentRobot().getModelId()+")");
            fireCompilationEvent(new ActionEvent(this,MasterMind.EVENTID_COMPILATION_NOCOMPILER,null));
        }
        
        // Start compilation procedure
        compiler.setupCompiler(commandList,getCurrentRobot());
        compiler.addEventListener(this);
        compiler.start();
    }
    
    
    /**
     * The RP6 Loader start has been requested. 
     */
    public void startRP6LoaderApplic() {
        
        log("Request to start RP6Loader external application.");
        String cmd=Configuration.getInstance().getProperty(ConfigProperties.RP6_LOADEREXE);
        if (cmd==null || cmd.isEmpty())
            log("-- Application path not configured!");
        else {
            try {
                //Runtime.getRuntime().exec("cmd /c start "+cmd);
                ProcessBuilder pb = new ProcessBuilder(cmd);
                processRP6Loader=pb.start();
            } 
            catch (IOException ex) {
                log("Error while starting application:"+cmd,ex);
                processRP6Loader=null;
            }
        }
    }
    
    
    /**
     * Handles all kind of <code>ActionEvent</code>s coming from all components
     * we observe (UI or not).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        int displayEventId=0;
        int compilerEventId=0;
        boolean executorEvent=false;
        
        String actionCommand=e.getActionCommand();
        
        switch(e.getID()) {
            
            case ProgramExecutor.EVENTID_EXECUTIONSTARTED: {
                executorEvent=true;
                log("");
                log("log.startnewsim");
                displayEventId=MasterMind.EVENTID_SIMULATION_STARTED;
                break;
            }
            case ProgramExecutor.EVENTID_PROGRAMENDED: {
                executor=null; // Dismiss Executor, which has cleaned-up himself
                executorEvent=true;
                log("log.currentposition",getCurrentVehiclePosition().toLogString());
                log("log.finishedsim");
                log("");
                displayEventId=MasterMind.EVENTID_SIMULATION_FINISHED;
                break;
            }
            case ProgramExecutor.EVENTID_COMMANDPOINTERADVANCED: {
                executorEvent=true;
                displayEventId=MasterMind.EVENTID_SIMULATION_NEWCOMMAND;
                log("log.currentposition",getCurrentVehiclePosition().toLogString());
                log("log.nextcommand",actionCommand);
                break;
            }
            case ProgramExecutor.EVENTID_COMMANDPARSINGFAILED: {
                executor=null; // Dismiss Executor
                executorEvent=true;
                displayEventId=MasterMind.EVENTID_SIMULATION_EXECUTIONERROR;
                log("log.finishedsiminerror"); log("");
                break;
            }
            case ProgramExecutor.EVENTID_EXECUTIONABORTED: {
                executor=null; // Dismiss Executor
                executorEvent=true;
                log("log.interruptsim"); log("");
                displayEventId=MasterMind.EVENTID_SIMULATION_ABORTED;
                break;
            }
            case ProgramExecutor.EVENTID_VEHICLEMOVED: {
                displayEventId=MasterMind.EVENTID_SIMULATION_VEHICULEMOVED;
                executorEvent=true; 
                break;
            }
            case ProgramExecutor.EVENTID_SIMULATIONACCELERATED: {
                log("log.acceleratesim");
                break;
            } 
            case ProgramExecutor.EVENTID_SIMULATIONNORMALSPEED: {
                log("log.normalspeedsim");
                break;
            } 
            case ProgramExecutor.EVENTID_SIMULATIONRESTARTED: {
                log("log.restartedsim");
                break;
            } 
            case ProgramCompiler.EVENTID_COMPILING_STARTED: {   
                log(""); log("log.compilationstart");
                compilerEventId=MasterMind.EVENTID_COMPILATION_STARTED;
                break;
            } 
            case ProgramCompiler.EVENTID_COMPILING_FAILED: {
                log("log.compilationfail"); log("");
                
                compilerEventId=MasterMind.EVENTID_COMPILATION_FAILED;
                compiler=null; 
                break;
            } 
            case ProgramCompiler.EVENTID_COMPILING_SUCCEEDED: {
                log("log.compilationsucceed"); log("");
                compilerEventId=MasterMind.EVENTID_COMPILATION_SUCCEEDED;
                compiler=null; 
                break;
            } 
            case ProgramCompiler.EVENTID_COMPILING_PARSINGERROR: {
                log("log.compilationparsingerror"); log("");
                compilerEventId=MasterMind.EVENTID_COMPILATION_PARSINGERROR;
                compiler=null; 
                break;
            } 
            case ProgramCompiler.EVENTID_COMPILING_CONFIGURATIONERROR: {
                log(e.getActionCommand()); log("");
                compiler=null; 
                break;
            } 
            case ProgramCompiler.EVENTID_COMPILING_OUTPUT: {
                compilerEventId=MasterMind.EVENTID_COMPILATION_OUTPUT;
                break;
            } 
        }
                
        // The 'executor' is just a temporary object, so we keep the position
        // for display after the simulation finished.
        if (executorEvent && executor!=null)
            currentVehiclePosition=executor.getCurrentVehiclePosition();
        
        // Distribute/forward events to interested parties
        if (displayEventId!=0)
            fireDisplayUpdate(new ActionEvent(this,displayEventId,actionCommand));
        if (compilerEventId!=0)
            fireCompilationEvent(new ActionEvent(this,compilerEventId,actionCommand));
    }
    
    
    /**
     * Forces the reload of the configuration file. Filename has been determined at start-up.
     */
    public void reloadConfigurationFile() {
        try {
            Configuration.getInstance().loadConfiguration();
        } 
        catch (IOException ex) {
            log("Failed",ex);
        }
        
        changeLanguage(I18N.getInstance().getLangCode());
        
        // Re-select the newly loaded objects by their key (which arent' supposed to change)
        selectRobot(Configuration.getInstance().getRobot(currentRobot.getKey()));
        selectLandscape(Configuration.getInstance().getLandscape(currentLandscape.getKey()));
    }
    
    
    /**
     * Activates a new robot.
     */
    public void selectRobot(Robot r) {
        
        currentRobot=r;
        // No need to stop current simulation, if any.
        fireDisplayUpdate(new ActionEvent(this,MasterMind.EVENTID_ROBOTCHANGED,null));
    }
    
    
    /**
     * Activates a new landscape.
     */
    public void selectLandscape(Landscape l) {
        
        currentLandscape=l;
        
        stopSimulation();
        currentVehiclePosition=getCurrentLandscape().getStartPosition();
                
        fireDisplayUpdate(new ActionEvent(this,MasterMind.EVENTID_LANDSCAPECHANGED,null));
    }
    
    
    /**
     * Fires the passed event to all display update listeners.
     */
    private void fireDisplayUpdate(ActionEvent event) {
        for (ActionListener l:displayUpdateListeners)
            l.actionPerformed(event);
    }
   
    
    /**
     * Fires the passed event to all display update listeners.
     */
    private void fireCompilationEvent(ActionEvent event) {
        for (ActionListener l:compilationNotificationListeners)
            l.actionPerformed(event);
    }
        
    /**
     * 
     */
    public void addDisplayUpdateListener(ActionListener l) {
        displayUpdateListeners.add(l);
    }

    
    /**
     * 
     */
    public void addLogMessageNotificationListener(ActionListener l) {
        logMessageNotificationListeners.add(l);
    }

    
    /**
     * 
     */
    public void addLanguageChangeNotificationListener(ActionListener l) {
        languageChangeNotificationListeners.add(l);
    }
    
    /**
     * 
     */
    public void addCompilationNotificationListener(ActionListener l) {
        compilationNotificationListeners.add(l);
    }

    
    
    /**
     * Commands to change the display language.
     */
    public void changeLanguage(String languageCode) {
        I18N.getInstance().setLangCode(languageCode);
        for (ActionListener l:languageChangeNotificationListeners)
            l.actionPerformed(new ActionEvent(this,MasterMind.EVENTID_GUILANGCHANGENOTIFICATION,languageCode));
    }
    
    
    /**
     * Outputs a message into the system log panel.
     */
    public void writeLog(String msg) {
        for (ActionListener l:logMessageNotificationListeners)
            l.actionPerformed(new ActionEvent(this,MasterMind.EVENTID_LOGMESSAGENOTIFICATION,msg));
    }
    
    
    /**
     * Outputs a message into the system log panel.
     */
    public void writeLog(String msg,Exception e) {
        
        StringBuilder sb=new StringBuilder(msg);
        sb.append("\n");
        
        _log(e,sb);
        
        for (ActionListener l:logMessageNotificationListeners)
            l.actionPerformed(new ActionEvent(this,MasterMind.EVENTID_LOGMESSAGENOTIFICATION,sb.toString()));
    }
    
    
    /**
     * Helper to output recursively output a stack trace into the log.
     */
    public void _log(Throwable t,StringBuilder sb) {
        
        StringWriter sw=null;
        PrintWriter pw=null;
        
        try {
            sw=new StringWriter();
            pw=new PrintWriter(sw);
            t.printStackTrace(pw);
        }
        finally {
            try { if (pw!=null) pw.close(); } catch(Exception e1) {}
            try { if (sw!=null) sw.close(); } catch(Exception e2) {}
        }
        sb.append(sw.toString());
        
        if (t.getCause()!=null)
            _log(t.getCause(),sb);
    }
    
    
    /**
     * Returns currently active landscape/mission.
     */
    public Landscape getCurrentLandscape() {
        return currentLandscape;
    }

    
    /**
     * Returns currently active robot.
     */
    public Robot getCurrentRobot() {
        return currentRobot;
    }
    
    
    /**
     * Returns current robot position, kept even after the simulation
     * has been concluded.
     */
    public VehicleState getCurrentVehiclePosition() {
        return currentVehiclePosition;
    }
}
