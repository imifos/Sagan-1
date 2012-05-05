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
package pro.carl.edu.sagan1.entity;

/**
 * Defines a vehicle and its state.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class VehicleState {
    
    /** X position in mm */
    private int x;
    
    /** Y position in mm */
    private int y;
    
    /** Pointing direction, angle in degrees in relation to +X axis */
    private int angle;
    
    /** Special flag allowing defining the different (animation) phase of 
     "sending a signal". 0 is the default value and setting the value >0
     make the robot sending a signal. The executor is responsable to decrease 
     this value to 0.*/
    private int signalState;
    
    /** Special flag allowing to put the vehicule in WAIT/IDLE state. false is the
     default value and the executor is responsable to reset to false after the 
     wait phase. */
    private boolean idleRunning;
     
    
    public VehicleState() {
        x=0;
        y=0;
        angle=0;
        signalState=0;
        idleRunning=false;
    }
    
    public VehicleState(VehicleState v) {
        x=v.getX();
        y=v.getY();
        angle=v.getAngle();
        signalState=v.getSignalState();
        idleRunning=v.isIdleRunning();
    }

    public VehicleState(int x,int y,int degrees) {
        this.x=x;
        this.y=y;
        this.angle=degrees;
        signalState=0;
        idleRunning=false;
    }
    
    public VehicleState(int x,int y) {
        this(x,y,0);
    }

    @Override
    public VehicleState clone() {
        return new VehicleState(this);
    }
    
    public String toLogString() {
        return "[X="+x+",Y="+y+",a="+angle+"]";
    }
    
    
    public int getAngle() {
        return angle;
    }
    
    public double getAngleRad() {
        return Math.toRadians(angle);
    }
    
    public void setAngle(int angle) {
        this.angle = angle;
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
    
    public void incX(int i) {
        x+=i;
    }

    public void decX(int i) {
        x-=i;
    }

    public void incY(int i) {
        y+=i;
    }

    public void decY(int i) {
        y-=i;
    }

    public int getSignalState() {
        return signalState;
    }

    public void setSignalState(int signalState) {
        this.signalState = signalState;
    }

    public boolean isIdleRunning() {
        return idleRunning;
    }

    public void setIdleRunning(boolean idleRunning) {
        this.idleRunning = idleRunning;
    }
 
    
}
