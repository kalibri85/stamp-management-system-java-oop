/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.npc;
import javax.swing.SwingUtilities;

/**
 *
 * @author lana
 */
public class NPC {

    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
