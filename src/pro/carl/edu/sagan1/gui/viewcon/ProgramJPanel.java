package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import pro.carl.edu.sagan1.logic.compiling.CompilationRunningException;
import pro.carl.edu.sagan1.logic.scripting.SimulationRunningException;
import pro.carl.edu.sagan1.gui.dialog.ProgrammingHelpDialog;
import pro.carl.edu.sagan1.logic.ConfigProperties;
import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;
import static pro.carl.edu.sagan1.logic.MasterMind.log;

/**
 * Program editor panel.
 * 
 * @since 0.0
 * @version 1.1.0
 */
public class ProgramJPanel extends JPanel implements ActionListener {
    
    private JScrollPane scrollEditorPanel;
    private JTextPane editorPanel;
    private JPanel buttonPanel;
    private JPanel helpTextPanel;
    private JLabel helpTextLine1;
    private JLabel helpTextLine2;
    private JLabel helpTextStatus;
    private JButton saveButton;
    private JButton helpButton;
    private JButton loadButton;
    private JButton startSimButton;
    private JButton stopSimButton;
    private JButton resetSimButton;
    private JButton compileButton;
                
    final private Font editorPanelFont=new Font(Font.MONOSPACED,Font.PLAIN,12);

    /** 
     * Constructor building up the panel outfit.
     */
    ProgramJPanel() {
        
        super(new BorderLayout());
        
        ActionListener myself=this;
        
        // Help Text 
        // ---------
        helpTextPanel=new JPanel(new BorderLayout(0,3));
        add(helpTextPanel,BorderLayout.NORTH);
        
        helpTextLine1=new JLabel(i18n("panel.program.helpline1")); 
        helpTextLine2=new JLabel(""); // Placeholder
        
        helpTextStatus=new JLabel("",SwingConstants.LEFT);
        helpTextStatus.setForeground(Color.red);
        helpTextStatus.setFont(editorPanelFont);
    
        helpTextPanel.add(helpTextLine1,BorderLayout.NORTH);
        helpTextPanel.add(helpTextLine2,BorderLayout.CENTER);
        helpTextPanel.add(helpTextStatus,BorderLayout.SOUTH);
         
        // Text Editor
        // -----------
        editorPanel=new JTextPane();
        editorPanel.setEditable(true);
        editorPanel.setFont(editorPanelFont);
        
        // The CENTER zone takes all the space not needed by other components, 
        // so set the text editor there to have it maximised.
        scrollEditorPanel = new JScrollPane(editorPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollEditorPanel,BorderLayout.CENTER);
        
        // Button Bar
        // ----------
        saveButton=new JButton("Save");
        saveButton.addActionListener(myself);
        
        loadButton=new JButton("Load");
        loadButton.addActionListener(myself);

        startSimButton=new JButton("Start Simulation");
        startSimButton.addActionListener(myself);
        
        stopSimButton=new JButton("Stop Simulation");
        stopSimButton.addActionListener(myself);
        stopSimButton.setEnabled(false);
        
        resetSimButton=new JButton("Reset Robot");
        resetSimButton.addActionListener(myself);
        resetSimButton.setEnabled(false);
        
        helpButton=new JButton("Help");
        helpButton.addActionListener(myself);
        
        compileButton=new JButton("Compile");
        compileButton.addActionListener(myself);
        
        buttonPanel = new JPanel(new GridLayout(4,3));
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(startSimButton);
        buttonPanel.add(stopSimButton);
        buttonPanel.add(resetSimButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(compileButton);

        add(buttonPanel,BorderLayout.SOUTH);
        
        updateGuiText();
        
        MasterMind.getInstance().addDisplayUpdateListener(myself);
        MasterMind.getInstance().addLanguageChangeNotificationListener(myself);
        MasterMind.getInstance().addCompilationNotificationListener(myself);
    }

    
    /**
     * Handles all kind of <code>ActionEvent</code>s coming from all components
     * we observe.
     */
    @Override
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void actionPerformed(ActionEvent e) {
        
        // Master Mind events, IDed by ID
        switch(e.getID()) {
            case MasterMind.EVENTID_SIMULATION_STARTED: {
                startSimButton.setEnabled(false);   stopSimButton.setEnabled(true);
                loadButton.setEnabled(false);       saveButton.setEnabled(false);
                resetSimButton.setEnabled(false);   helpTextStatus.setText("");
                return;
            }
            case MasterMind.EVENTID_SIMULATION_EXECUTIONERROR: {
                helpTextStatus.setText(i18n("statusmsg.prog.syntaxerr")+" "+e.getActionCommand());
                startSimButton.setEnabled(true);    stopSimButton.setEnabled(false);
                loadButton.setEnabled(true);        saveButton.setEnabled(true);
                resetSimButton.setEnabled(true);
                return;
            }
            case MasterMind.EVENTID_SIMULATION_FINISHED:
            case MasterMind.EVENTID_SIMULATION_ABORTED: {
                startSimButton.setEnabled(true);    stopSimButton.setEnabled(false);
                loadButton.setEnabled(true);        saveButton.setEnabled(true);
                resetSimButton.setEnabled(true);    helpTextStatus.setText("");
                return;
            }
            case MasterMind.EVENTID_SIMULATION_NEWCOMMAND:
            case MasterMind.EVENTID_SIMULATION_VEHICULEMOVED: {
                // Nothing to do
                return;
            }
            case MasterMind.EVENTID_GUILANGCHANGENOTIFICATION: {
                updateGuiText();
                return;
            }
            case MasterMind.EVENTID_COMPILATION_STARTED: {
                compileButton.setEnabled(false);
                return;
            }
            case MasterMind.EVENTID_COMPILATION_FAILED: 
            case MasterMind.EVENTID_COMPILATION_SUCCEEDED: {
                compileButton.setEnabled(true);
                return;
            }
            case MasterMind.EVENTID_COMPILATION_PARSINGERROR: {
                helpTextStatus.setText(i18n("statusmsg.prog.syntaxerr")+" "+e.getActionCommand());
                compileButton.setEnabled(true);
                return;
            }
            case MasterMind.EVENTID_COMPILATION_NOCOMPILER: {
                JOptionPane.showMessageDialog(this,i18n("statusmsg.prog.nocompiler"),i18n("panel.program.compile"),JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // UIComonent events, IDed by action command
        if (e.getSource()==loadButton) {
            loadFile();
        }
        else if (e.getSource()==saveButton) {
            saveFile();
        }
        else if (e.getSource()==startSimButton) {
            onStartSimButton();
        }
        else if (e.getSource()==stopSimButton) {
            MasterMind.getInstance().stopSimulation();
        }
        else if (e.getSource()==resetSimButton) {
            MasterMind.getInstance().resetVehiculeToStartPosition();
        }
        else if (e.getSource()==helpButton) {
            new ProgrammingHelpDialog();
        }
        else if (e.getSource()==compileButton) {
            onCompileButton();
        }
        
    }
    
    
    /**
     * Starts the graphical simulation.
     */
    private void onStartSimButton() {
        
        String[] lines=getProgramText().toUpperCase().split("\n");
        
        if (lines.length<1) {
            JOptionPane.showMessageDialog(this,i18n("panel.program.writeprogramfirst"),i18n("panel.program.startsim"),JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            MasterMind.getInstance().startSimulation(Arrays.asList(lines));
        } 
        catch (SimulationRunningException ex) {
            // Ignoring, for the user (should never happen)
            log("Cannot start simulation since there is already one running!");
            helpTextStatus.setText("Cannot start simulation since there is already one running!"); 
        }
    }
    
    /**
     * Starts the compilation of the sagan program into the target robots binary language.
     */
    private void onCompileButton() {
        
        String[] lines=getProgramText().toUpperCase().split("\n");
        if (lines.length<1) {
            JOptionPane.showMessageDialog(this,i18n("panel.program.writeprogramfirst"),i18n("panel.program.compile"),JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            MasterMind.getInstance().buildBinaryPackage(Arrays.asList(lines));
        } 
        catch (CompilationRunningException ex) {
            // Ignoring, for the user (should never happen)
            log("Cannot start compilation since there is already one running!");
            helpTextStatus.setText("Cannot start compilation since there is already one running!"); 
        }
    }
    
        
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
        saveButton.setText(i18n("panel.program.saveprog"));
        loadButton.setText(i18n("panel.program.loadprog"));
        startSimButton.setText(i18n("panel.program.startsim"));
        stopSimButton.setText(i18n("panel.program.stopsim"));
        resetSimButton.setText(i18n("panel.program.resetpos"));
        helpButton.setText(i18n("panel.program.help"));
        compileButton.setText(i18n("panel.program.compile"));
        helpTextLine1.setText(i18n("panel.program.helpline1")); 
        helpTextLine2.setText(i18n("panel.program.helpline2")); 
    }
    
    
    /**
     * Returns the (trimmed) content of the programming window.
     */
    public String getProgramText() {
        return editorPanel.getText().trim();
    }
    
            
    /**
     * Opens the file chooser and loads a program from the resulting file.
     */
    @SuppressWarnings("CallToThreadDumpStack")
    private void loadFile() {
        
        FileNameExtensionFilter fcFilter=new FileNameExtensionFilter(i18n("panel.program.filefilter"),"sagan1");
        
        JFileChooser fc=new JFileChooser();
        
        fc.setFileFilter(fcFilter);
        fc.setCurrentDirectory(new File(Configuration.getInstance().getProperty(ConfigProperties.DIR_USERPROGRAMS)));
         
        if (fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) 
            return; // cancel
        
        File file=fc.getSelectedFile();
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        StringBuilder sb=new StringBuilder();
        
        try {
            fileReader=new FileReader(file);
            bufferedReader=new BufferedReader(fileReader);
            
            String line=bufferedReader.readLine();
            while(line!=null) {
                sb.append(line);
                sb.append("\n");
                line=bufferedReader.readLine();
            }
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(this,i18n("panel.program.loaderror")+e.getMessage(),i18n("panel.program.load.title"),JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try { if (bufferedReader!=null) bufferedReader.close(); } catch(Exception e) { e.printStackTrace(); } // Output to console, not clean but efficient
            try { if (fileReader!=null) fileReader.close(); } catch(Exception e) { e.printStackTrace(); }         
        }
        
        editorPanel.setText(sb.toString().trim());
        // More verbose: JOptionPane.showMessageDialog(this,i18n("panel.program.loadok")+file.getAbsolutePath(),i18n("panel.program.load.title"),JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Opens the file chooser and saves the program into the resulting file.
     */
    @SuppressWarnings("CallToThreadDumpStack")
    private void saveFile() {
        
        FileNameExtensionFilter fcFilter=new FileNameExtensionFilter(i18n("panel.program.filefilter"),"sagan1");
        
        // JFileChooser with integrated overwrite confirmation dialogue 
        JFileChooser fc=new JFileChooser() {
            @Override
            public void approveSelection() {
                File f1=getSelectedFile();
                File f2=new File(f1.getAbsolutePath()+".sagan1");
                if( (f1.exists() || f2.exists()) && getDialogType()==SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,i18n("panel.program.save.oktooverwrite"),i18n("panel.program.save.title"),JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result) {
                        case JOptionPane.YES_OPTION:    super.approveSelection(); return;
                        case JOptionPane.NO_OPTION:     return;
                        case JOptionPane.CANCEL_OPTION: super.cancelSelection(); return;
                    }
                }
                super.approveSelection();
            }
        };

        fc.setFileFilter(fcFilter);
        fc.setCurrentDirectory(new File(Configuration.getInstance().getProperty(ConfigProperties.DIR_USERPROGRAMS)));
        
        if (fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) 
            return; // CANCEL!
        
        // Possibly add extension
        File file=fc.getSelectedFile();
        if (!file.getAbsolutePath().toLowerCase().endsWith(".sagan1"))
            file=new File(file.getAbsolutePath()+".sagan1");
                
        // Write file
        FileWriter fileWriter=null;
        BufferedWriter bufferedWriter=null;
        try {
            fileWriter=new FileWriter(file,false);
            bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(editorPanel.getText().trim());
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(this,i18n("panel.program.saveerror")+e.getMessage(),i18n("panel.program.save.title"),JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try { if (bufferedWriter!=null) bufferedWriter.close(); } catch(Exception e) { e.printStackTrace(); } 
            try { if (fileWriter!=null) fileWriter.close(); } catch(Exception e) { e.printStackTrace(); } 
        }
        
        // Confirm 
        JOptionPane.showMessageDialog(this,i18n("panel.program.saveok")+file.getAbsolutePath(),i18n("panel.program.save.title"),JOptionPane.INFORMATION_MESSAGE);
    }
}
