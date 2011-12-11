/* 
 * If it hits an obstacle it will stop the motors and two LEDs will start 
 * to blink.
 * It will stay in this state and wait until you restart the program!
 *
 * PLEASE PROVIDE FREE SPACE FOR THE ROBOT! 
 *
 * DO NOT FORGET TO REMOVE THE FLAT CABLE CONNECTION TO THE USB INTERFACE
 * BEFORE YOU START THIS PROGRAM BY PRESSING THE START BUTTON ON THE ROBOT!
 * (don't remove the USB Interface from the PC - only remove the flat cable
 * connection between the Interface and the Robot!)
 */

/*****************************************************************************/
// Includes:

#include "RP6RobotBaseLib.h"    // The RP6 Robot Base Library, always needs to be included!


void BUMPERS_stateChanged_empty(void) {
}

/**
 * Here we react on any obstacle that we may hit. 
 * If any of the bumpers detects an obstacle, we stop the motors and start
 * LED blink.
 */
void bumpersStateChanged(void) {
    
    if (bumper_left || bumper_right) {
        
        stop();
        moveAtSpeed(0, 0); // stop moving!
        
        setLEDs(0b111111);
        updateStatusLEDs();
        mSleep(500);
        
        while (true) {
            statusLEDs.LED2 = !statusLEDs.LED2; // Toggle LED bit in LED shadow register... 
            statusLEDs.LED5 = !statusLEDs.LED5;
            updateStatusLEDs();
            mSleep(500);
            task_RP6System();
        }
    }
}

void signal(void) {
    
    int c;
    for (c = 0; c < 3; c++) {
        setLEDs(0b100000);
        mSleep(200);
        setLEDs(0b010000);
        mSleep(200);
        setLEDs(0b001000);
        mSleep(200);
        setLEDs(0b000100);
        mSleep(200);
        setLEDs(0b000010);
        mSleep(200);
        setLEDs(0b000001);
        mSleep(200);
    }
    
    setLEDs(0b000000);
}

/*****************************************************************************/
// Main:

int main(void) {
    
    initRobotBase();

    setLEDs(0b111111);
    mSleep(1500);
    setLEDs(0b000000);

    // Set Bumpers state changed event handler:
    BUMPERS_setStateChangedHandler(bumpersStateChanged);

    powerON(); // Turn Encoders, Motor Current Sensors 
    // ATTENTION: Automatic Motor control will not work without this!

    /* RP6 SAGAN GENERATED COMMANDS START */
    
    /*{SAGAN1_COMMANDS_HERE}*/
    
    /* RP6 SAGAN GENERATED COMMANDS STOP */

    stop();
    moveAtSpeed(0, 0);
    BUMPERS_setStateChangedHandler(BUMPERS_stateChanged_empty);
    setLEDs(0b000000);

    while (true) {
        statusLEDs.LED2 = !statusLEDs.LED2; // Toggle LED bit in LED shadow register... 
        statusLEDs.LED5 = !statusLEDs.LED5;
        updateStatusLEDs();
        mSleep(500);
        task_RP6System();
    }
    return 0;
}
