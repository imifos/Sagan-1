package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.entity.Robot;
import pro.carl.edu.sagan1.gui.component.ImageJPanel;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;

/**
 * Robot selection panel.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class RobotJPanel extends JPanel implements ActionListener,ComponentListener {
    
    private ButtonGroup buttonGroup;
    private JRadioButton robotButtons[];
    private String selectedRobotKey="";
    private JLabel helpLineLabel1;
    private JLabel helpLineLabel2;
    private ImageJPanel picLabel;

    
    /** 
     * Layouts the panel.
     */
    public RobotJPanel() {
        
        super(new BorderLayout());
        
        RobotJPanel myself=this;
        
        Collection<Robot> robots=Configuration.getInstance().getAllRobots();
        
        JPanel topPanel=new JPanel(new GridLayout(5+robots.size(),0));
        add(topPanel,BorderLayout.NORTH);
        JPanel spacePanel=new JPanel(new GridLayout(1,0));
        spacePanel.add(Box.createVerticalGlue());
        add(spacePanel,BorderLayout.CENTER);
        
        // Help Text 
        // ---------
        helpLineLabel1=new JLabel();
        helpLineLabel2=new JLabel();
        topPanel.add(helpLineLabel1);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(helpLineLabel2);
        
        // Robot Image
        // -----------
        picLabel=new ImageJPanel();
        picLabel.setSize(getWidth(),getHeight()/5);
        add(picLabel,BorderLayout.SOUTH);

        // Selection
        // ---------
        robotButtons=new JRadioButton[robots.size()];
        buttonGroup=new ButtonGroup();
        int ndx=0;
        
        for (Robot r:robots) {
            
            robotButtons[ndx]=new JRadioButton();
            robotButtons[ndx].setText(r.getName());
            robotButtons[ndx].setSelected(ndx==0 ? true:false);
            robotButtons[ndx].setActionCommand(r.getKey());
            robotButtons[ndx].addActionListener(myself);
            
            buttonGroup.add(robotButtons[ndx]);
            topPanel.add(robotButtons[ndx]);
            ndx++;
        }
        
        // First selection
        if (ndx>0) {
            selectedRobotKey=robotButtons[0].getActionCommand();
            Robot currentRobot=Configuration.getInstance().getRobot(selectedRobotKey);
            MasterMind.getInstance().selectRobot(currentRobot);
        }
        
        updateGuiText();
        
        MasterMind.getInstance().addLanguageChangeNotificationListener(myself);
    }
    
    
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
        
        Robot cr=MasterMind.getInstance().getCurrentRobot();
        if (cr.getRobotPicture()!=null)
            picLabel.setImage(cr.getRobotPicture());
        else picLabel.setImage(null);
        
        helpLineLabel1.setText(i18n("panel.robot.helpline1"));
        helpLineLabel2.setText(i18n("panel.robot.helpline2"));
    }
   
    
    /**
     * Call-back for combo button selection. 
     */
    @Override
    public void actionPerformed(ActionEvent e) { 
        
        // GUI Language change
        if (e.getID()==MasterMind.EVENTID_GUILANGCHANGENOTIFICATION) {
            updateGuiText();
            return;
        }
        
        // Avoid handling re-click on the selected robot
        if (selectedRobotKey.equals(e.getActionCommand()))
            return; 
        else selectedRobotKey=e.getActionCommand();
        
        // Update the current state
        Robot currentRobot=Configuration.getInstance().getRobot(selectedRobotKey);
        MasterMind.getInstance().selectRobot(currentRobot);
        
        // Update picture
        Robot cr=MasterMind.getInstance().getCurrentRobot();
        if (cr.getRobotPicture()!=null)
            picLabel.setImage(cr.getRobotPicture());
        else picLabel.setImage(null);
        picLabel.setSize(getWidth(),getHeight()/5);
        repaint();
    }

    
    @Override
    public void componentResized(ComponentEvent e) {
        // Resize robot picture
        picLabel.setSize(getWidth(),getHeight()/5);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

}
