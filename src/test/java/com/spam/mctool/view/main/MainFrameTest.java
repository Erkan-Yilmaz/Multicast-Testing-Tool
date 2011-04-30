/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main;

import org.uispec4j.Button;
import org.uispec4j.Window;
import com.spam.mctool.controller.Controller;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Table;
import org.uispec4j.Trigger;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

/**
 *
 * @author Tobias
 */
public class MainFrameTest extends UISpecTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
        setAdapter(new MainClassAdapter(Controller.class, new String[0]));
    }
    
    /**
     * Test creating a Sender
     */
    @Test
    public void testCreatingASender() throws Exception {
        System.out.println("creating a sender");

        // Retrieve the components
        Window window = getMainWindow();
        Table table = window.getTable("senderTable");
        Button addSenderButton = window.getButton("buAddSender");

        // Check that the sender table is empty and displays the proper
        // column names
        assertTrue(table.getHeader().contentEquals(new String[] {
            "Status", "Sender ID", "Port", "Multicast Group", "Conf. Packet Rate",
            "Measured Packet Rate", "Min. Packet Rate", "Max. Packet Rate"
        }));
        assertTrue(table.isEmpty());

        // Click on the "Add Sender" button and intercept the dialog
        WindowInterceptor.init(addSenderButton.triggerClick())
        .process(new WindowHandler() {
            public Trigger process(Window dialog) {
                assertTrue(dialog.titleEquals("Create Sender"));
                dialog.getCheckBox("activateBox").select();
                return dialog.getButton("okButton").triggerClick();
            }
        })
        .run();
        
        // Check that a new row is displayed in the table
        assertEquals(table.getRowCount(), 1);

    }

}