package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import javax.swing.Timer;

import pro.carl.edu.sagan1.entity.Circle;
import pro.carl.edu.sagan1.entity.Rectangle;
import pro.carl.edu.sagan1.entity.Form;
import pro.carl.edu.sagan1.entity.Landscape;
import pro.carl.edu.sagan1.entity.Mark;
import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.entity.VehicleState;
import pro.carl.edu.sagan1.gui.shape.robot.RobotShapeRegistry;
import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;

/**
 * Simulation panel responsible for simulation display.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class SimulationJPanel extends JPanel implements ActionListener {
    
    /** Number of seconds the last status message is displayed before clearing it. */
    private static final int STATUS_MESSAGE_DISPLAY_TIME_SEC=30;
    
    /** Space between drawing zone border and landscape border. */
    private static final double PADDING=10.0D;
        
    /** X pixel botton-left position of 0,0 transforming from top-left as reference point */
    private int x0px=0;
    /** Y pixel botton-left position of 0,0 transforming from top-left as reference point */
    private int y0px=0;
        
    /** Current landscape. */
    private Landscape landscape=null;       
    
    /** Current robot. */
    private Robot robot=null;       
   
    private VehicleState currentRobotPosition;

    /** @see SimulationJPanel#computeDisplayParameters() */
    private double aspectFactor=1.0D;
    
    /** Last status message, cleared after XX minutes. */
    private String statusMessage="";
    
    /** Timer to clear last status message. */
    private Timer statusMessageTimer;
    
    /** Status message font, create once - use anytime. */
    private Font statusMessageFont=new Font("Sans Serif",Font.BOLD,12);
    
    /** Mouse coordinates message font, create once - use anytime. */
    private Font mouseposMessageFont=new Font("Sans Serif",Font.PLAIN,10);
    
    /** Map title and copyright message font, create once - use anytime. */
    private Font mapTitleMessageFont=new Font("Sans Serif",Font.BOLD,11);
    
    /** Mouse position within the drawing frame, not yet converted to mm relative to the map. */
    private Point mousePos=new Point(0,0);
    
    
    /**
     * Default constructor to initialise the environment.
     */
    SimulationJPanel() {
        
        super(true);
        
        SimulationJPanel myself=this;
        
        updateGuiText();
        
        // Capture mouse movement to display coordinates
        addMouseMotionListener(new MouseMotionListener() {
            @Override 
            public void mouseMoved(MouseEvent e) {
                if (Configuration.getInstance().showMouseCoordinates()) {
                    mousePos.setLocation(e.getX(),e.getY());
                    repaint(11,10,100,40);
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {}
        });
        
        MasterMind.getInstance().addDisplayUpdateListener(myself);
        MasterMind.getInstance().addLanguageChangeNotificationListener(myself);
    }
 
    
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
    }
    
    
    /**
     * Call-back handling the PAINT event, drawing the simulation and all 
     * what's around.
     */
    @Override 
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);    
        
        Graphics2D g2=(Graphics2D)g;
        
        if (landscape!=null) {
            computeDisplayParameters(g2);
            drawLandscape(g2);
            drawRobot(g2);
        }
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Status message
        if (statusMessage!=null && !statusMessage.isEmpty()) {
            g2.setColor(Color.WHITE);
            g2.setFont(statusMessageFont);
            String m=i18n("statusmsg.prefix")+statusMessage;
            g2.drawString(m,11,20); // Not very elegant
            g2.drawString(m,9,20);  // but works fine :)
            g2.drawString(m,10,19);
            g2.drawString(m,10,21);
            g2.setColor(Color.RED);
            g2.setFont(statusMessageFont);
            g2.drawString(m,10,20);
        }
        
        // Mouse position
        if (landscape!=null && Configuration.getInstance().showMouseCoordinates()) {
            // computeDisplayParameters() must had have been called!
            g2.setFont(mouseposMessageFont);
            g2.setColor(Color.BLACK);
            g2.drawString("X:"+(int)(pix2mm(mousePos.getX()-x0px))+",Y:"+(int)(pix2mm(y0px-mousePos.getY())),11,40);
        }
    }
    
    
    /**
     * Calculates the display parameters like the number of pixels per mm in 
     * function of the drawing zone size and the reference point of the drawing 
     * zone in  function of the size.
     * <p/>
     * Calculates aspect factors for both X and Y axis, then takes the smaller 
     * option in order to have the entire landscape drawed in the available 
     * space keeping the right proportions.
     */
    private void computeDisplayParameters(Graphics2D g) {
        
        double factorW=((double)(getWidth()-PADDING*2))  / ((double)landscape.getWidth());
        double factorH=((double)(getHeight()-PADDING*2)) / ((double)landscape.getHeight());
        
        if (factorW<factorH) aspectFactor=factorW; else aspectFactor=factorH;
        
        // Calculate the pixel botton-left position of 0,0 transforming from top-left as reference point
        x0px=(int)(0.0+PADDING+(getWidth()-PADDING*2-landscape.getWidth()*aspectFactor)/2);
        y0px=(int)(getHeight()-PADDING-(getHeight()-PADDING-landscape.getHeight()*aspectFactor)/2);
    }
    
    
    /**
     * Converts the passed mm value to pixel using the pre-calculated aspect factor.
     * @see SimulationJPanel#computeDisplayParameters(java.awt.Graphics2D) 
     */
    private int mm2px(double mm) {
        return (int)(mm*aspectFactor);
    }
    
    /**
     * Converts the passed pixel value to mm using the pre-calculated aspect factor.
     * @see SimulationJPanel#computeDisplayParameters(java.awt.Graphics2D) 
     */
    private double pix2mm(double pixel) {
        return (double)(pixel/aspectFactor);
    }
    
    
    /**
     * Draws the current landscape which is composed by known shapes.
     */
    private void drawLandscape(Graphics2D g) {
                
        double landWpix=mm2px(landscape.getWidth());
        double landHpix=mm2px(landscape.getHeight());
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background image
        if (landscape.getBackgroundImage()!=null) {
             g.drawImage(landscape.getBackgroundImage(), 
                     (int)x0px,(int)(y0px-landHpix),(int)(x0px+landWpix),(int)(y0px-landHpix+landHpix), 
                     0, 0, landscape.getBackgroundImage().getWidth(),landscape.getBackgroundImage().getHeight(),null);
        }
        
        // Draw surrounding rectangle
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        g.drawRect( (int)x0px,(int)(y0px-landHpix),(int)landWpix,(int)landHpix);
        
        // Draw all inside shapes
        g.setStroke(new BasicStroke(1));
        for (Form form:landscape.getForms()) {
            
            switch(form.getType()) {
                case CIRCLE: {
                    Circle c=(Circle)form;  
                    g.setColor(Color.GREEN);
                    g.drawOval(x0px+mm2px(c.getX()),
                               y0px-mm2px(c.getY())-mm2px(c.getDiam()),
                               mm2px(c.getDiam()),mm2px(c.getDiam()));
                    break;
                }
                case RECTANGLE: {
                    Rectangle r=(Rectangle)form;
                    g.setColor(Color.BLUE);
                    g.drawRect(x0px+mm2px(r.getX()),                 // X
                               y0px-mm2px(r.getY())-mm2px(r.getH()), // Y
                               mm2px(r.getW()),                      // W
                               mm2px(r.getH()));                     // H
                    break;
                }
                case TASKMARK: {
                    Mark m=(Mark)form;
                    int W=100,H=100;
                    AffineTransform at=g.getTransform();
                    g.rotate(Math.toRadians(m.getAngle()),x0px+mm2px(m.getX()),y0px-mm2px(m.getY()));
                    g.setStroke(new BasicStroke(1));
                    g.setColor(Color.PINK);
                    g.drawRect(x0px+mm2px(m.getX()-W/2),y0px-mm2px(m.getY()+H/2),mm2px(H),mm2px(W));                
                    g.drawLine(x0px+mm2px(m.getX()-W/2),y0px-mm2px(m.getY()+H/2),
                               x0px+mm2px(m.getX()-W/2+W/2),y0px-mm2px(m.getY()+H/2+20));
                    g.drawLine(x0px+mm2px(m.getX()-W/2+W/2),y0px-mm2px(m.getY()+H/2+20),
                               x0px+mm2px(m.getX()-W/2+W),y0px-mm2px(m.getY()+H/2));
                    g.setFont(new Font("Sans Serif",Font.PLAIN,12));
                    g.drawString(m.getText(),x0px+mm2px(m.getX()-5),y0px-mm2px(m.getY()));
                    g.setTransform(at);
                    break;
                }
                default: 
                    throw new IllegalArgumentException("Unrecognised shape form:"+form.getType());
                    
            } // switch shape type
        } // for all shapres
        
        // Title
        g.setFont(mapTitleMessageFont);
        if (landscape.getBackgroundImageCredit()==null) {
            g.setColor(Color.WHITE);
            String t=i18n(landscape.getKey());
            g.drawString(t,(int)x0px+10,(int)y0px-10);
            g.drawString(t,(int)x0px+12,(int)y0px-12);
            g.drawString(t,(int)x0px+10,(int)y0px-12);
            g.drawString(t,(int)x0px+10,(int)y0px-12);
            g.setColor(Color.BLACK);
            g.drawString(t,(int)x0px+11,(int)y0px-11);
        }
        else {
            g.setColor(Color.WHITE);
            String c=i18n(landscape.getKey())+"    /  Image Credit: "+landscape.getBackgroundImageCredit();
            g.drawString(c,(int)x0px+10,(int)y0px-10); // not elegant,
            g.drawString(c,(int)x0px+12,(int)y0px-12); // but easy and
            g.drawString(c,(int)x0px+12,(int)y0px-10); // efficient
            g.drawString(c,(int)x0px+10,(int)y0px-12);
            g.setColor(Color.BLACK);
            g.drawString(c,(int)x0px+11,(int)y0px-11);
        }
        
    } // drawLandscape
    
   
    /**
     * Draws the currently active robot and potientially special actions as well.
     */
    private void drawRobot(Graphics2D g) {

        if (robot==null) 
            return;

        if (currentRobotPosition!=null) {
            RobotShapeRegistry.getShape(robot.getModelId()).draw(aspectFactor,x0px,y0px,currentRobotPosition,g);
        }
        else {
            RobotShapeRegistry.getShape(robot.getModelId()).draw(aspectFactor,x0px,y0px,landscape.getStartPosition(),g);
        }
    }

    
    /**
     * Invoked in case of outside events this object is observing.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        boolean simulationEvent=false;
        
        switch(e.getID()) {
            case MasterMind.EVENTID_ROBOTCHANGED: {
                setRobot(MasterMind.getInstance().getCurrentRobot());
                break;
            }
            case MasterMind.EVENTID_LANDSCAPECHANGED: {
                setLandscape(MasterMind.getInstance().getCurrentLandscape());
                break;
            }
            case MasterMind.EVENTID_SIMULATION_STARTED: {
                setStatusMessage("statusmsg.sim.started");
                simulationEvent=true;
                break;
            }
            case MasterMind.EVENTID_SIMULATION_FINISHED: {
                setStatusMessage("statusmsg.sim.ended");
                simulationEvent=true;
                break;
            }
            case MasterMind.EVENTID_SIMULATION_ABORTED: {
                setStatusMessage("statusmsg.sim.aborted");
                simulationEvent=true;
                break;
            }
            case MasterMind.EVENTID_SIMULATION_NEWCOMMAND: {
                simulationEvent=true;
                break;
            }
            case MasterMind.EVENTID_SIMULATION_EXECUTIONERROR: {
                setStatusMessage("statusmsg.sim.syntaxerr",e.getActionCommand());
                simulationEvent=true;
                break;
            }
            case MasterMind.EVENTID_SIMULATION_VEHICULEMOVED: {
                simulationEvent=true;
                break;
            }
            case MasterMind.EVENTID_GUILANGCHANGENOTIFICATION: {
                updateGuiText();
                break;
            }
        }
        
        if (simulationEvent) {
            currentRobotPosition=MasterMind.getInstance().getCurrentVehiclePosition();
        }
        
        repaint();
    }
    

    /**
     * Activates a new Landscape.
     */
    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }

    
    /**
     * Activates a new robot model.
     */
    public void setRobot(Robot robot) {
        this.robot = robot;
    }
    
    
    /**
     * Updates the status message and sets a timer to clean the message after a given time.
     */
    private void setStatusMessage(String statusMessageKey) {
        setStatusMessage(statusMessageKey,null);
    }
    
    /**
     * Updates the status message and sets a timer to clean the message after a given time.
     */
    private void setStatusMessage(String statusMessageKey,String command) {
    
        if (statusMessageKey==null)
            statusMessage="";
        else {
            StringBuilder sb=new StringBuilder(i18n("statusmsg.prefix"));
            sb.append(' ').append(i18n(statusMessageKey));
            if (command!=null) sb.append(command);
            statusMessage=sb.toString();
        }
        repaint(9,19,800,50);
        
        // Start clean-up timer
        if (statusMessageTimer==null || !statusMessageTimer.isRunning()) {
            
            statusMessageTimer=new Timer(1000*STATUS_MESSAGE_DISPLAY_TIME_SEC,new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    statusMessageTimer.stop();
                    statusMessageTimer=null;
                    setStatusMessage(null);
                    repaint(); // repaint since this is most likely the last screen output
                }
            });
            statusMessageTimer.setRepeats(false);
            statusMessageTimer.start();
        }
        else {
            statusMessageTimer.restart();
        }
    }
  
}
