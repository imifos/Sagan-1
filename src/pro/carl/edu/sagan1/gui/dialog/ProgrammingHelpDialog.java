package pro.carl.edu.sagan1.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.parsing.Commands;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;
import static pro.carl.edu.sagan1.logic.MasterMind.log;


/**
 * Layouts the programming help dialogue.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class ProgrammingHelpDialog extends JDialog {
 
    /**
     * Set-ups the dialogue.
     */
    public ProgrammingHelpDialog() {
        
        super();
       
        JLabel txt=new JLabel(buildCommandHelp());
        getContentPane().add(txt,BorderLayout.CENTER);
        
        JButton button = new JButton(i18n("dialog.programhelp.closebutton")); 
        getContentPane().add(button,BorderLayout.SOUTH);
        
        button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                setVisible(false); 
                dispose(); 
            }
        });

        setIconImage(Configuration.getInstance().getAppIcon());
        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle(i18n("panel.program.help"));
        setLocationRelativeTo(null); // Centers the dialog
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximumSize(new Dimension(500,500));
        setMinimumSize(new Dimension(50,300));
        setResizable(false);
        
        pack(); 
        setVisible(true);
    }

    
    /**
     * Installs ESC button handler.
     */
    @Override
    protected JRootPane createRootPane() {
        
        Action keyListener = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                dispose();
            }
        };
        
        JRootPane myRootPane=new JRootPane();
        KeyStroke stroke=KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap=myRootPane.getInputMap();
        inputMap.put(stroke,"ESCAPE");
        myRootPane.getActionMap().put("ESCAPE",keyListener);
        return myRootPane;
    }
    
    
    /**
     * Constructs a string containing the HTML programming help text.
     */
    private String buildCommandHelp() {
                
        StringBuilder sb=new StringBuilder("<html><table cellpadding=10><tr><td>");
        sb.append(i18n("dialog.programhelp.text1")).append("<p/>").append(i18n("dialog.programhelp.text2")).append("<br/><ul>");
        
        int cnt=0;
        Commands[] cmds=Commands.values();
        for (Commands c:cmds) {
            sb.append("<li><b>").append(c.name()).append("</b>: ").append(i18n(c.getHelpTxtId()));
            if ((cnt++)<cmds.length-1) sb.append("</li>");
        }
        sb.append("</ul><p>").append(i18n("dialog.programhelp.text3")).append("<br/></td></tr></table></html>");
        return sb.toString();
    }
}
