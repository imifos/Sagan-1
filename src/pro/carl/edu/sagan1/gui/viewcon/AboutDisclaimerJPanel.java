package pro.carl.edu.sagan1.gui.viewcon;

import com.sun.servicetag.SystemEnvironment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import pro.carl.edu.sagan1.logic.About;
import pro.carl.edu.sagan1.logic.MasterMind;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;

/**
 * The About/License/Disclaimer panel, displays also the system configuration on request.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class AboutDisclaimerJPanel extends JPanel implements ActionListener {
   
    private JScrollPane scrollInfoArea;
    private JEditorPane infoPane;
    private JPanel buttonPanel;
    private JPanel helpTextPanel;
    private JLabel helpTextLine;
    private JButton refreshButton;
        
    final private SimpleDateFormat infoDateFormat=new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    
    
    /** 
     * Constructor building up the panel outfit.
     */
    AboutDisclaimerJPanel() {
        
        super(new BorderLayout());
        
        AboutDisclaimerJPanel myself=this;
        
        // Help Text 
        // ---------
        helpTextPanel=new JPanel(new BorderLayout(0,3));
        add(helpTextPanel,BorderLayout.NORTH);
        
        helpTextLine=new JLabel();
        helpTextPanel.add(helpTextLine,BorderLayout.NORTH);
         
        // Output Zone
        // -----------
        infoPane=new JEditorPane();
        infoPane.setEditable(false);
        infoPane.setEditorKit(new HTMLEditorKit());
        infoPane.setPreferredSize(new Dimension(200,200));
        
        scrollInfoArea=new JScrollPane(infoPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollInfoArea,BorderLayout.CENTER);
        
        // Button Bar
        // ----------
        refreshButton=new JButton("Update");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                updateInfo();
            }
        });
        
        buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel,BorderLayout.SOUTH);
        
        updateGuiText();
        MasterMind.getInstance().addLanguageChangeNotificationListener(myself);
    }

    
    /**
     * Updates the GUI screen texts in the current language.
     */
    private void updateGuiText() {
        refreshButton.setText("Update");
        infoPane.setText(About.ABOUT+"<br/><hr><br/>Please click the 'Update' button.");
        helpTextLine.setText(i18n("panel.sysinfo.helpline"));
    }
    

    
    /**
     * Handles all kind of <code>ActionEvent</code>s coming from all components
     * we observe.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID()==MasterMind.EVENTID_GUILANGCHANGENOTIFICATION) {
            updateGuiText();
            return;
        }
    }
     
    
    /**
     * Gathers all kind of system information and sets the info text.
     */
    private void updateInfo() {
        
        int MB=1024*1024;
        
        StringBuilder sb=new StringBuilder(About.ABOUT);
                
        sb.append("<br/><br/><hr>");
        sb.append("<b><u>SYSTEM INFORMATION</u></b><br/><br/>");
        
        sb.append("Free memory (MB): ").append(Runtime.getRuntime().freeMemory()/MB).append("<br/>");

        long maxMemory = Runtime.getRuntime().maxMemory();
        sb.append("Maximum memory (MB): ").append(maxMemory == Long.MAX_VALUE ? "No limit" : maxMemory/MB).append("<br/>");

        sb.append("Total memory (MB): ").append(Runtime.getRuntime().totalMemory()/MB).append("<br/>");

        sb.append("Non-heap memory (Bytes): ").append(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().toString()).append("<br/>");
        sb.append("System Load Average: ").append(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()).append("<br/>");
    
        File[] roots = File.listRoots();
        for (File root : roots) {
            sb.append("<br/>File system root: ").append(root.getAbsolutePath()).append("<br/>");
            sb.append("Total space (MB): ").append(root.getTotalSpace()/MB).append("<br/>");
            sb.append("Free space (MB): ").append(root.getFreeSpace()/MB).append("<br/>");
            sb.append("Usable space (MB): ").append(root.getUsableSpace()/MB).append("<br/>");
        }

        sb.append("<br/><br/>");
            
        SystemEnvironment se = SystemEnvironment.getSystemEnvironment();
        sb.append("CPU Manufacturer: ").append(se.getCpuManufacturer()).append("<br/>");
        sb.append("Host Name: ").append(se.getHostname()).append("<br/>");
        sb.append("OS Architecture: ").append(se.getOsArchitecture()).append("<br/>");
        sb.append("OS Name: ").append(se.getOsName()).append("<br/>");
        sb.append("OS Verstion: ").append(se.getOsVersion()).append("<br/>");
        sb.append("Available processors (cores): ").append(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
                
        sb.append("<br/><br/><u>System settings:</u><br/>");
        for (Map.Entry<Object,Object> e:System.getProperties().entrySet()) {
            if (e.getKey().toString().startsWith("java") || e.getKey().toString().startsWith("os") || e.getKey().toString().startsWith("user"))
                sb.append("*").append(e.getKey().toString()).append("=").append(e.getValue().toString()).append("<br/>");
        }
        sb.append("<br/>");
        infoPane.setText(sb.toString());
    }
    
}
