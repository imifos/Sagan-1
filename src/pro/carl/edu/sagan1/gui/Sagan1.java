/*
* Sagan-1 Robot Simulator
* -----------------------
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* A copy of the GNU General Public License can be found here:
* http://www.gnu.org/licenses/.
*
* Author:
* Tasha CARL, 2011, http://lucubratory.eu / http://sagan-project.eu
*/
package pro.carl.edu.sagan1.gui;

import pro.carl.edu.sagan1.logic.Versions;
import pro.carl.edu.sagan1.logic.Configuration;
import pro.carl.edu.sagan1.logic.MasterMind;

import pro.carl.edu.sagan1.gui.viewcon.MainJFrame;

import java.io.IOException;


/**
 * SAGAN 1 - Robot Simulator - Entry Point.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 * @author Tasha CARL
 */
public class Sagan1 {

    /**
     * Entry point.
     */
    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(String[] args) {
        
        System.out.println(Versions.APP_TITLE+"   "+Versions.APP_VERSION);
        System.out.println("--------------------------------------------");
        System.out.println("Starting...");
        System.out.println();
             
        // Load config
        try {
            Configuration.getInstance().loadConfiguration();
        }
        catch (IOException e) {
            System.out.println("Trouble loading the configuration properties! Stop here!");
            System.out.flush();
            e.printStackTrace();
            return;
        }
        catch (Exception e) {
            System.out.println("Trouble parsing the configuration properties! Stop here!");
            System.out.flush();
            e.printStackTrace();
            return;
        }
        
        Configuration.getInstance().outRuntimeInfo();
        
        /* Set the Nimbus look and feel */
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         * Metal,Nimbus,CDE/Motif,Windows,Windows Classic
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Trouble setting the SWING look and feel! Stop here!");
            System.out.flush();
            ex.printStackTrace();
            return;
        } 
                
        System.out.println("And here we go...");
                
        // Warm-up the master controller
        MasterMind.getInstance().onStartUpHeadless();
        
        // Start-up Swing GUI
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }
}
