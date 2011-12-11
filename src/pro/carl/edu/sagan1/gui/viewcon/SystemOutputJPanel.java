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
 * Application log panel.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class SystemOutputJPanel extends JPanel implements ActionListener {
    
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
        
    /** Prefix of the log message and time format. Don't toutch the '|['. 
     @see pro.carl.edu.robotikids.rp6.mcp.gui.viewcon.SystemOutputJPanel.LogDocument */
    final private SimpleDateFormat logDateFormat=new SimpleDateFormat("|[dd-MM-yy HH:mm:ss.SSS] ");
    
    
    /** 
     * Constructor building up the panel outfit.
     */
    SystemOutputJPanel() {
        
        super(new BorderLayout());
        
        SystemOutputJPanel myself=this;
        
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
        logArea.setLineWrap(false);  
        logArea.setWrapStyleWord(false);  
                
        // The CENTER zone takes all the space not needed by other components, 
        // so set the text editor there to have it maximised.
        scrollLogArea=new JScrollPane(logArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollLogArea,BorderLayout.CENTER);
        
        // Button Bar
        // ----------
        clearButton=new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                logArea.setText("");
                log("System and application log cleared.");
            }
        });
        
        buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        add(buttonPanel,BorderLayout.SOUTH);
        
        MasterMind.getInstance().addLogMessageNotificationListener(myself);
    }

    
    /**
     * Handles all kind of <code>ActionEvent</code>s coming from all components
     * we observe.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        switch(e.getID()) {
            case MasterMind.EVENTID_LOGMESSAGENOTIFICATION: {
                if (e.getActionCommand()==null || e.getActionCommand().isEmpty())
                    logArea.append("\n");
                else {
                    logArea.append(logDateFormat.format(new Date()));
                    logArea.append(e.getActionCommand());
                    logArea.append("\n");
                }
                break;
            }
        }

    }
}
