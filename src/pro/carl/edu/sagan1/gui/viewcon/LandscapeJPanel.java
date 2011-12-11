package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import pro.carl.edu.sagan1.entity.Landscape;
import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;

/**
 * Mission and Landscape selection panel.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class LandscapeJPanel extends JPanel implements ActionListener {
    
    private ButtonGroup buttonGroup;
    private JRadioButton landscapeButtons[];
    private String selectedLandscapeKey="";
    private JLabel helpLineLabel1;
    private JLabel helpLineLabel2;
    private JLabel missionTextLabel;
    
    /** 
     * Layouts the panel.
     */
    public LandscapeJPanel() {
        
        super(new BorderLayout());
        
        LandscapeJPanel myself=this;
        
        Collection<Landscape> landscapes=Configuration.getInstance().getAllLandscapes();
        
        JPanel topPanel=new JPanel(new GridLayout(5+landscapes.size(),0));
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
        
        // Selection
        // ---------
        landscapeButtons=new JRadioButton[landscapes.size()];
        buttonGroup=new ButtonGroup();
        int ndx=0;
        
        for (Landscape l:landscapes) {
            
            landscapeButtons[ndx]=new JRadioButton();
            landscapeButtons[ndx].setText(i18n(l.getKey()));
            landscapeButtons[ndx].setActionCommand(l.getKey());
            landscapeButtons[ndx].addActionListener(myself);
            landscapeButtons[ndx].setSelected(ndx==landscapes.size()-1 ? true:false);
            
            buttonGroup.add(landscapeButtons[ndx]);
            topPanel.add(landscapeButtons[ndx]);
            ndx++;
        }
        
        // First selection
        if (ndx>0) {
            selectedLandscapeKey=landscapeButtons[ndx-1].getActionCommand();
            Landscape currentLandscape=Configuration.getInstance().getLandscape(selectedLandscapeKey);
            MasterMind.getInstance().selectLandscape(currentLandscape);
        }
        
        missionTextLabel=new JLabel();
        add(missionTextLabel,BorderLayout.SOUTH);
        updateGuiText();
        
        MasterMind.getInstance().addLanguageChangeNotificationListener(this);
    }

    
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
        
        helpLineLabel1.setText(i18n("panel.landscape.helpline1"));
        helpLineLabel2.setText(i18n("panel.landscape.helpline2"));
        
        Collection<Landscape> landscapes=Configuration.getInstance().getAllLandscapes();
        int ndx=0;
        for (Landscape l:landscapes)
           landscapeButtons[ndx++].setText(i18n(l.getKey()));
        
        missionTextLabel.setText(i18n("mission."+MasterMind.getInstance().getCurrentLandscape().getKey()));
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
        
        // Avoid handling re-click on the selected landscape
        if (selectedLandscapeKey.equals(e.getActionCommand()))
            return; 
        
        // Update the current state, with all 
        selectedLandscapeKey=e.getActionCommand();
        Landscape currentLandscape=Configuration.getInstance().getLandscape(selectedLandscapeKey);
        MasterMind.getInstance().selectLandscape(currentLandscape);
        updateGuiText();
    }
}
