package pro.carl.edu.sagan1.logic.scripting;

import pro.carl.edu.sagan1.logic.parsing.SingleCommand;
import pro.carl.edu.sagan1.logic.parsing.ScriptLineParser;
import pro.carl.edu.sagan1.logic.parsing.ScriptParsingException;
import pro.carl.edu.sagan1.logic.Configuration;

import pro.carl.edu.sagan1.entity.VehicleState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import java.util.List;
import java.util.ArrayList;




/**
 * Executes the current SAGAN program and sends events to all observers in
 * function of the operation to perform. Robot movements are divided into
 * small intermediary steps, which are executed one by one using a timer.
 * <p/>
 * The time determines the animation speed.
 * <p/>
 * The mathematical division of each step is performed within the 
 * SingleCommand class.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class ProgramExecutor {
    
    static public final int EVENTID_EXECUTIONSTARTED=232967;
    static public final int EVENTID_EXECUTIONABORTED=867313;
    static public final int EVENTID_VEHICLEMOVED=324712435;
    static public final int EVENTID_PROGRAMENDED=8732967;
    static public final int EVENTID_COMMANDPOINTERADVANCED=99173871;
    static public final int EVENTID_COMMANDPARSINGFAILED=1639127;
    static public final int EVENTID_SIMULATIONACCELERATED=367532114;
    static public final int EVENTID_SIMULATIONNORMALSPEED=92167114;
    static public final int EVENTID_SIMULATIONRESTARTED=9216715;
    
    
    /** Contains the program code, line per line. */
    private List<String> program;
    
    /** Current SAGAN programm line. */
    private int currentLine;
    
    /** Line number where the last error has been detected. */
    private int lineInError;
    /** Last command in error. */
    private String commandInError;
    /** Error message describing the last error. */
    private String errorMessage;
    
    /** Object enclapsulating a single command. Holds all logic needed to 
    mathematical divide the command into single steps. Use for simulation only. */
    private SingleCommand currentCommand;
    
    /** Indicates of the simulation execution is accelerated. If yes, no 
    intermediary steps are calculated. */
    private boolean acceleratedExecution;
    
    /** Holds the running position of the robot during the simulation. */
    private VehicleState currentVehiclePosition;
    /** Holds the start position of the robot. */
    private VehicleState startVehiclePosition;
    
    /** Animation timer. */
    private Timer timer;
    
    /** List of registered event listeners. */
    private List<ActionListener> eventListeners=new ArrayList<ActionListener>();
    
    
    /**
     * Constructor.
     */
    public ProgramExecutor(List<String> progLines,VehicleState startPosition) {
        this.program=progLines;
        this.startVehiclePosition=startPosition;
        reset();
    }
    
    
    /**
     * Adds an observer. 
     */
    public void addEventListener(ActionListener l) {
        eventListeners.add(l);
    }

    /**
     * Starts the execution of the application.
     */
    public void start() {
    
        reset(); // Prepare engines...
        
        // ... inform everybody ...
        fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_EXECUTIONSTARTED,null));
        
        // ... and start the execution/animation timer!
        timer=new Timer(Configuration.getInstance().getAnimationTimerFrequency(),new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (currentCommand!=null)
                    // Currently executing a command making a smooth animation
                    executeCurrentCommand(); 
                else // Finish the current command, new start the next command in list
                    parseNextCommand(); 
            }
        });
        
        timer.setInitialDelay(100); // (give time to observers to warm-up)
        timer.setRepeats(true);
        timer.start();
        
        // Note: Use of Swing timers is required when UI components are involved
        // because of the timer event must be executed in the Swing event 
        // dispatching thread.
    }
    
    /**
     * Stops the currently running simulation, if there is any.
     */
    public void stop() {
    
        boolean wasInSimulation=currentLine!=-1;
        
        reset();
        
        if (wasInSimulation)
            fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_EXECUTIONABORTED,null));
    }
    
    
    /**
     * Resets the class into a state as required to (re)start program
     * execution.
     */
    private void reset() {
        
        if (timer!=null && timer.isRunning())
            timer.stop();
        
        timer=null;
        currentLine=-1;
        lineInError=-1;
        commandInError="";
        currentCommand=null;
        currentVehiclePosition=startVehiclePosition;
        acceleratedExecution=false;
    }

    /**
     * Advances one step in the animation sequence of the current command.
     * Invoked by the timer.
     */
    private void executeCurrentCommand() {

        // If all intermediary steps have been executed, just remove the 
        // current command in order to parse the next one at next timer event.
        if (!currentCommand.hasMoreSteps()) {
            currentCommand=null;
        }
        else {
            // Keep moving thru the intermediary steps (to keep animation on screen)
            currentVehiclePosition=currentCommand.doNextStep();
            fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_VEHICLEMOVED,null));
        }
    }
    
    /**
     * The animation sequence of the previous command has been completed and now we 
     * switch to the next command.
     */
    private void parseNextCommand() {
        
        timer.stop();
        currentLine++;
        
        // End of program
        if (currentLine>=program.size()) {
            // Clean-up resources and send 'over-and-out'. Likelily this object
            // will be dismissed now.
            reset(); 
            fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_PROGRAMENDED,null));
            return;
        }

        // Parse next command
        ScriptLineParser scriptParser=new ScriptLineParser();
        try {
            currentCommand=scriptParser.parse(program.get(currentLine),currentLine);
        } 
        catch (ScriptParsingException ex) {
            // Syntax error, stop!
            reset();
            lineInError=ex.getPos();
            commandInError=ex.getLine();
            errorMessage=ex.getMessage();
            fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_COMMANDPARSINGFAILED,(Integer.toString(lineInError+1)+" ["+commandInError+"] "+errorMessage)));
            return;
        }
        
        if (currentCommand!=null) {
            // NULL is legal when the current program line is empty or a comment
            // In this case, the timer will simple go to the next command on the 
            // next tick.
            if (currentCommand.isMetaCommand()) {
                // Command to the simulator
                switch(currentCommand.getCommandInstruction()) {
                    case $FASTON:
                        acceleratedExecution=true;
                        fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_SIMULATIONACCELERATED,null));
                        break;
                    case $FASTOFF:
                        acceleratedExecution=false;
                        fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_SIMULATIONNORMALSPEED,null));
                        break;
                    case $RESTART:
                        acceleratedExecution=false;
                        currentLine=-1;
                        currentVehiclePosition=startVehiclePosition;
                        fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_SIMULATIONRESTARTED,null));
                        break;
                }
            }
            else {
                // Command to simulate
                currentCommand.executeCommandCalculation(currentVehiclePosition,acceleratedExecution);
                fireEvent(new ActionEvent(this,ProgramExecutor.EVENTID_COMMANDPOINTERADVANCED,Integer.toString(currentLine+1)+" - "+currentCommand.toLogString()));
            }
        }
        
        // Start the time in any case, either to execute the current command or switch to the next 
        // if the current should be ignored.
        timer.start();
    }

    
    /**
     * Fires the passed event to all listeners.
     */
    private void fireEvent(ActionEvent event) {
        for (ActionListener l:eventListeners)
            l.actionPerformed(event);
    }
    
    
    /**
     * Note: the object is a clone of the real position, so modifying will not
     * change the position on screen.
     */
    public VehicleState getCurrentVehiclePosition() {
        return currentVehiclePosition.clone();
    }
    
    
    /**
     * Returns the line number where the error occured or -1 if no error has
     * been detected.
     */
    public int getLineNumberInError() {
        return lineInError;
    }
    
    
    /**
     * Returns the command text where the error occured or "" if no error has
     * been detected.
     */
    public String getCommandInError() {
        return commandInError;
    }
    
    
    /**
     * Returns the program line where the error occured or NULL if no error has
     * been detected.
     */
    public String getLineInError() {
        if (lineInError!=-1)
            return program.get(lineInError);
        return null;
    }
}
