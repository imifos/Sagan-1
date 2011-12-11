package pro.carl.edu.sagan1.gui.viewcon;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;
import static pro.carl.edu.sagan1.logic.MasterMind.log;

/**
 * Communication panel which outputs status of cross-compilation and communication
 * output (stdout, stderr).
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class CommunicationJPanel extends JPanel implements ActionListener {
    
    static final int MAX_LOG_MEMORY_KB=512; // Must be >25
    
    /**
     * Underlying document of the JTextArea to monitor size.
     */
    static class LogDocument extends PlainDocument {
        
        LogDocument() {
            super();
        }

        @Override
        public void insertString(int offs,String str,AttributeSet a) throws BadLocationException {
            // If the log text becomes too big, remove the earliest parts, keeping the log line complete.
            if (getLength()>MAX_LOG_MEMORY_KB*1024) {
                String t=getText(1024*24,300);
                int p=t.lastIndexOf("|[");
                remove(0,p==-1 ? MAX_LOG_MEMORY_KB*1024 : 1024*24+p);
            }
            super.insertString(offs, str, a);
        }
    }
    
    private JScrollPane scrollLogArea;
    private JTextArea logArea;
    private JPanel buttonPanel;
    private JPanel helpTextPanel;
    private JLabel helpTextLine;
    private JButton clearButton;
    private JButton rp6loaderButton;
    
        
    /** Prefix of the log message and time format. Don't touch the '|['. 
     @see pro.carl.edu.robotikids.rp6.mcp.gui.viewcon.SystemOutputJPanel.LogDocument */
    final private SimpleDateFormat logDateFormat=new SimpleDateFormat("|[dd-MM-yy HH:mm:ss.SSS] ");
    
    
    /** 
     * Constructor building up the panel outfit.
     */
    CommunicationJPanel() {
        
        super(new BorderLayout());
        
        CommunicationJPanel myself=this;
        
        // Help Text 
        // ---------
        helpTextPanel=new JPanel(new BorderLayout(0,3));
        add(helpTextPanel,BorderLayout.NORTH);
        
        helpTextLine=new JLabel(i18n("panel.sysout.helpline"),SwingConstants.LEFT);
        helpTextPanel.add(helpTextLine,BorderLayout.NORTH);
         
        // Output Zone
        // -----------
        logArea=new JTextArea();
        logArea.setDocument(new LogDocument());
        logArea.setEditable(false);
        logArea.setLineWrap(true);  
        logArea.setWrapStyleWord(true);  
                
        // The CENTER zone takes all the space not needed by other components, 
        // so set the text editor there to have it maximised.
        scrollLogArea=new JScrollPane(logArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollLogArea,BorderLayout.CENTER);
        
        // Button Bar
        // ----------
        clearButton=new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent evt) {
                logArea.setText("");
            }
        });
        
        rp6loaderButton=new JButton("Start RP6Loader");
        rp6loaderButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent evt) {
                MasterMind.getInstance().startRP6LoaderApplic();
            }
        });
        
        buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(rp6loaderButton);
        add(buttonPanel,BorderLayout.SOUTH);
        
        MasterMind.getInstance().addCompilationNotificationListener(myself);
    }

    
    /**
     * Handles all kind of <code>ActionEvent</code>s coming from all components
     * we observe.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        switch(e.getID()) {
     
            case MasterMind.EVENTID_COMPILATION_STARTED: {
                logArea.setText(""); // cls
                logArea.append("\n\n");
                logArea.append(logDateFormat.format(new Date()));
                logArea.append("Compilation and packaging started.\n");
                break;
            }
            case MasterMind.EVENTID_COMPILATION_FAILED: {
                logArea.append("\n");
                logArea.append(logDateFormat.format(new Date()));
                logArea.append("Compilation failed. Error shown above or in the system log.\n\n");
                break;
            }
            case MasterMind.EVENTID_COMPILATION_SUCCEEDED: {
                logArea.append("\n");
                logArea.append(logDateFormat.format(new Date()));
                logArea.append("Compilation succeeded. Output shown above.\n\n");
                break;
            }
            case MasterMind.EVENTID_COMPILATION_PARSINGERROR: {
                logArea.append(logDateFormat.format(new Date()));
                logArea.append("User program parsing error. Please correct first.\n\n");
                break;
            }
            case MasterMind.EVENTID_COMPILATION_OUTPUT: {
                logArea.append(e.getActionCommand());
                logArea.append("\n");
                break;
            }
        }
    }
    
    
}