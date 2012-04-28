package pro.carl.edu.sagan1.entity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;


import static pro.carl.edu.sagan1.logic.MasterMind.log;


/**
 * Defines one single robot entity.
 * 
 * @since 0.0
 * @version 1.0.1 
 */
public class Robot {
    
    private String key;
    private String name;
    private String modelId;
    private String pictureFilePath;
    
    private BufferedImage robotPicture;
    
    private double rp6CalibrationEncoderResolution;
    private double rp6CalibrationRotationFactor;
    
    private int nxtCalibrationUnitRotationTime;
    private int nxtCalibrationUnitMovementTime;
    private double nxtCalibrationTimePerDegree;     // must be 'double' to avoid rounding error 
    private double nxtCalibrationTimePerMillimeter; // accumulations due to division then re-multiplication.
    private boolean nxtUsesCompassSensor;
    
    /**
     * Constructor.
     */
    public Robot(String key) {
        this.key=key;
    }
    
    /**
     * Sets the robot image path and loads the picture in memory, if possible.
     */
    public void setPictureFilePath(String pictureFilePath) {
        
        this.pictureFilePath=pictureFilePath;
        this.robotPicture=null;
        
        if (pictureFilePath==null || pictureFilePath.isEmpty())
            return;
        
        try {                
            robotPicture=ImageIO.read(new File(this.pictureFilePath));
        } 
        catch (IOException ex) {
            System.out.println("Unable to load robot image with key: "+key+", continue without! File:"+this.pictureFilePath+", Error:"+ex.getMessage());
            log("Unable to load robot image with key: "+key+", continue without! File:"+this.pictureFilePath+", Error:"+ex.getMessage());
            this.robotPicture=null;
        }
    }
    
    
    
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public double getRp6CalibrationEncoderResolution() {
        return rp6CalibrationEncoderResolution;
    }

    public void setRp6CalibrationEncoderResolution(double calibrationEncoderResolution) {
        this.rp6CalibrationEncoderResolution = calibrationEncoderResolution;
    }

    public double getRp6CalibrationRotationFactor() {
        return rp6CalibrationRotationFactor;
    }

    public void setRp6CalibrationRotationFactor(double calibrationRotationFactor) {
        this.rp6CalibrationRotationFactor = calibrationRotationFactor;
    }
   
    public BufferedImage getRobotPicture() {
        return robotPicture;
    }

    public int getNxtCalibrationUnitMovementTime() {
        return nxtCalibrationUnitMovementTime;
    }

    public void setNxtCalibrationUnitMovementTime(int nxtCalibrationUnitMovementTime) {
        this.nxtCalibrationUnitMovementTime = nxtCalibrationUnitMovementTime;
    }

    public int getNxtCalibrationUnitRotationTime() {
        return nxtCalibrationUnitRotationTime;
    }

    public void setNxtCalibrationUnitRotationTime(int nxtCalibrationUnitRotationTime) {
        this.nxtCalibrationUnitRotationTime = nxtCalibrationUnitRotationTime;
    }

    public double getNxtCalibrationTimePerDegree() {
        return nxtCalibrationTimePerDegree;
    }

    public void setNxtCalibrationTimePerDegree(double nxtCalibrationTimePerDegree) {
        this.nxtCalibrationTimePerDegree = nxtCalibrationTimePerDegree;
    }

    public double getNxtCalibrationTimePerMillimeter() {
        return nxtCalibrationTimePerMillimeter;
    }

    public void setNxtCalibrationTimePerMillimeter(double nxtCalibrationTimePerMillimeter) {
        this.nxtCalibrationTimePerMillimeter = nxtCalibrationTimePerMillimeter;
    }

    public boolean isNxtUsesCompassSensor() {
        return nxtUsesCompassSensor;
    }

    public void setNxtUsesCompassSensor(boolean nxtUsesCompassSensor) {
        this.nxtUsesCompassSensor = nxtUsesCompassSensor;
    }

}
