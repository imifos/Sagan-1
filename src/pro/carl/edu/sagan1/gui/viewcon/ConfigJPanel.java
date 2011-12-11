package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;
import static pro.carl.edu.sagan1.logic.MasterMind.log;

/**
 * Application configuration panel. Allows to change language and reload the 
 * properties files.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class ConfigJPanel extends JPanel implements ActionListener {
    
    private JPanel configPanel;
    private JLabel helpTextLine;
    private JComboBox langCombo;
    private JButton reloadConfigButton;
    
    /** List of alailable languages, key/value pairs. */
    private List<Configuration.Language> languages;
    
    
    /** 
     * Constructor building up the panel outfit.
     */
    ConfigJPanel() {
        
        super(new BorderLayout());
        
        configPanel=new JPanel(new BorderLayout(0,3));
        add(configPanel,BorderLayout.NORTH);
        
        helpTextLine=new JLabel();
        configPanel.add(helpTextLine,BorderLayout.NORTH);
        
        languages=Configuration.getInstance().getLanguages();
                
        langCombo = new JComboBox(languages.toArray());
        langCombo.setSelectedIndex(0);
        langCombo.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Configuration.Language lang=(Configuration.Language)((JComboBox)e.getSource()).getSelectedItem();
                log("log.selectlang",lang.toString());
                MasterMind.getInstance().changeLanguage(lang.getCode());
            }
        });

        configPanel.add(langCombo,BorderLayout.CENTER);
        
        reloadConfigButton=new JButton("Reload Configuration from disk");
        configPanel.add(reloadConfigButton,BorderLayout.SOUTH);
        reloadConfigButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                MasterMind.getInstance().reloadConfigurationFile();
            }
        });
        
        
        updateGuiText();
        
        // So we can get notified if language changes...
        MasterMind.getInstance().addLanguageChangeNotificationListener(this);
    }

    
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
        helpTextLine.setText(i18n("panel.config.helpline"));
    }
    
    
    /**
     * Handles all kind of <code>ActionEvent</code>s coming from all components
     * we observe.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID()==MasterMind.EVENTID_GUILANGCHANGENOTIFICATION) 
            updateGuiText();
    }

}
