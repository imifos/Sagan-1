/*
* Sagan-1 Robot Simulator
* -----------------------
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* A copy of the GNU General Public License can be found here:
* http://www.gnu.org/licenses/.
*
* Author:
* Tasha CARL, 2011, http://lucubratory.eu / http://sagan-project.eu
*/
package pro.carl.edu.sagan1.logic.parsing;

import pro.carl.edu.sagan1.entity.VehicleState;

import java.util.ArrayList;
import java.util.List;

import pro.carl.edu.sagan1.logic.Configuration;



/**
 * Ecapsulates one single SAGAN script command and ALL information about its 
 * SIMULATION.
 * <p/>
 * Holds all logic allowing a command to be executed in the simulator. By taking 
 * the instruction as input, it returns a list of steps to execute for the 
 * animation at "timer" speed. In case of an accelerated simulation, the list 
 * just contains start and end point.
 * <p/>
 * The knowledge about robot hardware related commands is located in the 
 * specific compiler classes.
 *  
 * @since 0.0
 * @version 1.1.0 - 26/04/2012
 */
public class SingleCommand {
    
    /* Command instruction. */
    private Commands command;
    /* Command parameter mm). */
    private int distance=-1;
    /* Command parameter (degrees). */
    private int degrees=-1;
    /* Command parameter (ms). */
    private int time=-1;   
    
    /** List of generated intermediary steps, if this applies (simulation only). */
    List<VehicleState> steps=new ArrayList<VehicleState>();
    
    
    /**
     * Constructor.
     */
    public SingleCommand(Commands command,Integer distance,Integer degrees,Integer time) {
        this.command=command;
        this.distance=(distance==null ? -1 : distance.intValue());
        this.degrees=(degrees==null ? -1 : degrees.intValue());
        this.time=(time==null ? -1 : time.intValue());
    }

    
    /**
     * Executes the command into the "steps" micro-step list when target is the 
     * SAGAN simulator.
     */
    public void executeCommandCalculation(VehicleState originVehiclePosition,boolean accelerated) {
        
        VehicleState p=originVehiclePosition.clone();
        
        if (command.equals(Commands.TURNRIGHT)) {
            
            if (!accelerated) {
                for (int i=0;i<degrees-3;i+=5) {
                    p.setAngle(originVehiclePosition.getAngle()+i);
                    steps.add(p);
                    p=p.clone();
                }
            }
            p.setAngle(originVehiclePosition.getAngle()+degrees);
            steps.add(p);
        }
        //
        else if (command.equals(Commands.TURNLEFT)) {
            
            if (!accelerated) {
                for (int i=0;i<degrees-3;i+=5) {
                    p.setAngle(originVehiclePosition.getAngle()-i);
                    steps.add(p);
                    p=p.clone();
                }
            }
            p.setAngle(originVehiclePosition.getAngle()-degrees);
            steps.add(p);
        }
        //
        else if (command.equals(Commands.FORWARD)) {
                        
            int x1=p.getX();
            int y1=p.getY();
            int x2=(int)( x1+ ((double)distance)*Math.sin(p.getAngleRad()) );
            int y2=(int)( y1+ ((double)distance)*Math.cos(p.getAngleRad()) );
            bresenhamLine(x1,y1,x2,y2,p,accelerated);
        }
        //
        else if (command.equals(Commands.BACKWARD)) {
                        
            int x1=p.getX();
            int y1=p.getY();
            int x2=(int)( x1- ((double)distance)*Math.sin(p.getAngleRad()) );
            int y2=(int)( y1- ((double)distance)*Math.cos(p.getAngleRad()) );
            bresenhamLine(x1,y1,x2,y2,p,accelerated);
        }
        //
        else if (command.equals(Commands.SENDSIGNAL)) {
            
            // The position of the vehicule doesn't change but we add some steps 
            // in order to animate the 'send signal' on the screen.
            for (int i=(accelerated ? 4:0);i<=4;i++) {
                p.setSignalState(1+i);
                steps.add(p);
                p=p.clone();
            }        
            p.setSignalState(0); // last operation must be a reset to 0.
            steps.add(p);
        }
        else if (command.equals(Commands.WAIT)) {
            
            // In fact we don't touch the vehicle for X timer ticks. The flag is 
            // just 'pro forma' to have the state object representing the real state.
            for (int i=time;i>0;i-=Configuration.getInstance().getAnimationTimerFrequency()) {
                p.setIdleRunning(true);
                steps.add(p);
                p=p.clone();
            }
            p.setIdleRunning(false); // Last operation must be removing idle mode
            steps.add(p);
        }
    }

    
    /**
     * Calculates all points of a direct line between 2 points using the Bresenham 
     * algorithm.
     * <p/>
     * @link http://de.wikipedia.org/wiki/Bresenham-Algorithmus
     * @link http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
     */
    private void bresenhamLine(int x1,int y1,int x2, int y2,VehicleState p,boolean accelerated) {
        
        int w=x2-x1;
        int h=y2-y1;
        int dx1=0,dy1=0,dx2=0,dy2=0;
        
        if (w<0) dx1=-1; else if (w>0) dx1=1;
        if (h<0) dy1=-1; else if (h>0) dy1=1;
        if (w<0) dx2=-1; else if (w>0) dx2=1;
        
        int longest=Math.abs(w);
        int shortest=Math.abs(h);
        
        if (!(longest>shortest)) {
            longest=Math.abs(h);
            shortest=Math.abs(w);
            if (h<0) dy2=-1; else if (h>0) dy2=1;
            dx2=0;            
        }
        int numerator=longest>>1;
        
        for (int i=0;i<=longest && !accelerated;i++) {
        
            if (Math.abs(p.getX()-x1)>10 || Math.abs(p.getY()-y1)>10) {
                p.setX((int)x1);
                p.setY((int)y1);
                steps.add(p);
                p=p.clone();
            }
                
            numerator+=shortest;
            if (!(numerator<longest)) {
                numerator-=longest;
                x1+=dx1;
                y1+=dy1;
            } 
            else {
                x1+=dx2;
                y1+=dy2;
            }
        }
        
        // Final position = destination position
        p.setX((int)x2);
        p.setY((int)y2);
        steps.add(p);
    }
    
    
    /**
     * Returns the next step in the animation sequence, in case the target is 
     * the simulator.
     */
    public VehicleState doNextStep() {
        VehicleState p=steps.get(0);
        steps.remove(0);
        return p;
    }
    

    /**
     * Verifies if there are more steps in the animation sequence, in case the target is 
     * the simulator.
     */
    public boolean hasMoreSteps() {
        return steps!=null && !steps.isEmpty();
    }
    
    
    /**
     * Returns a string prepared for logging.
     */
    public String toLogString() {
        if (distance!=-1) return command.name()+"("+distance+")";
        if (degrees!=-1) return command.name()+"("+degrees+")";
        if (time!=-1) return command.name()+"("+time+")";
        return command.name()+"()";
    }

    
    /**
     * Verifies if the current command is a meta command. Meta commands are 
     * addressing the simulator only and have no effet on the hardware.
     */
    public boolean isMetaCommand() {
        return command.isMetaCommand();
    }
    
    
    /**
     * Returns the currently encapsulated instruction.
     */
    public Commands getCommandInstruction() {
        return command;
    }

    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    
}
