package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import pro.carl.edu.sagan1.logic.Versions;

import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.logic.MasterMind.log;
import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;

/**
 * Main screen frame.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class MainJFrame extends JFrame implements ActionListener {

    private SimulationJPanel jPanelSimulation;
    private ProgramJPanel jPanelProgram;
    private LandscapeJPanel jPanelLocation;
    private RobotJPanel jPanelRobot;
    private SystemOutputJPanel jPanelLog;
    private AboutDisclaimerJPanel jPanelSysInfo;
    private JPanel jPanelCommunication;
    private ConfigJPanel jPanelConfig;
    
    private JSplitPane jMainSplitPane;
    private JTabbedPane jMainInputTabbedPane;
    private JTabbedPane jMainOutputTabbedPane;
    
    private BufferedImage appIcon;
    
    
    /** 
     * Creates new form MainJFrame 
     */
    public MainJFrame() {
        
        super();
        
        setTitle(Versions.APP_TITLE+" - "+Versions.APP_VERSION);

        appIcon=null;
        try {
            appIcon=ImageIO.read(new File("appicon.png"));
        } 
        catch (IOException e) {
            log("Info: Unable to load app icon 'appicon.png'");
            System.out.println("Warning: Unable to load app icon 'appicon.png'");
        }
        if (appIcon!=null) {
            setIconImage(appIcon);
            Configuration.getInstance().setAppIcon(appIcon);
        }

        initComponents();
                
        // Set the screen size
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        setMaximizedBounds(e.getMaximumWindowBounds());
        setSize(e.getMaximumWindowBounds().width, e.getMaximumWindowBounds().height);
        //setResizable(false);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        
        MasterMind.getInstance().onStartUpWithGuiUp();
    }

    
    /** 
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int maxWidth=e.getMaximumWindowBounds().width;
        
        // Main Screen - Right Part - OUTPUT - Simulation drawing and logging zone
        jMainOutputTabbedPane = new JTabbedPane(); 
        jMainOutputTabbedPane.setMinimumSize(new Dimension(200,99));

        jPanelSimulation = new SimulationJPanel();
        jPanelLog = new SystemOutputJPanel();
        jPanelSysInfo= new AboutDisclaimerJPanel();
        jMainOutputTabbedPane.addTab("Simulation", jPanelSimulation);
        jMainOutputTabbedPane.addTab("System messages", jPanelLog);
        jMainOutputTabbedPane.addTab("System information", jPanelSysInfo);
                
        // Main Screen - Left Part - INPUT - Program and config zone
        jMainInputTabbedPane = new JTabbedPane(); 
        jMainInputTabbedPane.setMaximumSize(new Dimension(2000,99999));
        jMainInputTabbedPane.setMinimumSize(new Dimension(200,99));
        jMainInputTabbedPane.setPreferredSize(new Dimension(maxWidth/4,99999));
        
        jPanelLocation = new LandscapeJPanel();
        jPanelProgram = new ProgramJPanel();
        jPanelRobot = new RobotJPanel();
        jPanelCommunication= new CommunicationJPanel();
        jPanelConfig=new ConfigJPanel();
        
        jMainInputTabbedPane.addTab("Program", jPanelProgram);
        jMainInputTabbedPane.addTab("Robot", jPanelRobot);
        jMainInputTabbedPane.addTab("Communication", jPanelCommunication);
        jMainInputTabbedPane.addTab("Location", jPanelLocation);
        jMainInputTabbedPane.addTab("Configuration", jPanelConfig);
        
        // Main Screen
        jMainSplitPane = new JSplitPane();
        jMainSplitPane.setRightComponent(jMainOutputTabbedPane);
        jMainSplitPane.setLeftComponent(jMainInputTabbedPane);
        add(jMainSplitPane);
        pack();
    
        updateGuiText();
        
        // Close Main window confirmaiton dialogue
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(null,i18n("dialog.closeconfirmation.message"),
                    Versions.APP_TITLE,JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                   
                    backupCurrentProgram();
                    MasterMind.getInstance().onCloseApplication();
                    e.getWindow().dispose();
                    System.exit(0); //calling the method is a must
                }
            }
        });
        
        MasterMind.getInstance().addLanguageChangeNotificationListener(this);
        MasterMind.getInstance().addCompilationNotificationListener(this);
    }
    
    
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
        
        jMainOutputTabbedPane.setTitleAt(0,i18n("paneltab.simulation"));
        jMainOutputTabbedPane.setTitleAt(1,i18n("paneltab.systemmessages"));
        jMainOutputTabbedPane.setTitleAt(2,i18n("paneltab.systeminformation"));
        
        jMainInputTabbedPane.setTitleAt(0,i18n("paneltab.program"));
        jMainInputTabbedPane.setTitleAt(1,i18n("paneltab.robot"));
        jMainInputTabbedPane.setTitleAt(2,i18n("paneltab.communication"));
        jMainInputTabbedPane.setTitleAt(3,i18n("paneltab.location"));
        jMainInputTabbedPane.setTitleAt(4,i18n("paneltab.configuration"));
    }
    
    
    /**
     * Call-back for combo button selection. 
     */
    @Override
    public void actionPerformed(ActionEvent e) { 
        
        switch(e.getID()) {
            case MasterMind.EVENTID_GUILANGCHANGENOTIFICATION:
                updateGuiText();
                break;
            case MasterMind.EVENTID_COMPILATION_STARTED:
                jMainInputTabbedPane.setSelectedComponent(jPanelCommunication);
                break;
            case MasterMind.EVENTID_COMPILATION_PARSINGERROR:
                jMainInputTabbedPane.setSelectedComponent(jPanelProgram);
                break;
        }
    }
    
    
    /**
     * Write the current content of the programm editor into the file 
     * a back-up file. We do this since some kids had closed the 
     * simulator by accident loosing all their work. 
     */
    private void backupCurrentProgram() {
        
        String prog=jPanelProgram.getProgramText().trim();
        if (prog.isEmpty())
            return;
        
        FileWriter fileWriter=null;
        BufferedWriter bufferedWriter=null;
        try {
            fileWriter=new FileWriter("last_program_backup.sagan1",false);
            bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(prog);
        }
        catch(IOException e) {
            System.out.println("Trouble while writing the program back-up file:"+e.getMessage());
        }
        finally {
            try { if (bufferedWriter!=null) bufferedWriter.close(); } catch(Exception e) {  } 
            try { if (fileWriter!=null) fileWriter.close(); } catch(Exception e) {  } 
        }
    }    
}
