/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view;

import com.spam.mctool.model.Sender;
import com.spam.mctool.view.main.MainFrame;
import javax.swing.UIManager;

/**
 *
 * @author Tobias Stöckel
 */
public class McViewTest {

    public static void main (String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MctoolView view = new GraphicalView();
        view.init(null);
    }

}
