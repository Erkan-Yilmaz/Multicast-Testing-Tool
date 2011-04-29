/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view;

import java.io.File;

import javax.swing.JFileChooser;

import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.Table;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.ComponentUtils;

import com.spam.mctool.controller.Controller;

/**
 * If you run into problems with an UnsatisfiedLinkError
 * Use oracle's jvm.
 * See http://tech.groups.yahoo.com/group/uispec4j/message/820
 *
 * @author Konstantin Weitz
 */
public class ProfileTest extends UISpecTestCase {
    /**
     * Test creating a Sender
     */
    @Test
    public void testStoringAndLoadingAProfile() throws Exception {
    	setAdapter(new MainClassAdapter(Controller.class, new String[0]));
    	
        // Retrieve the components
        Window window = getMainWindow();
        Button addSenderButton = window.getButton("buAddSender");

        // Click on the "Add Sender" button and intercept the dialog
        WindowInterceptor.init(addSenderButton.triggerClick())
        .process(new WindowHandler() {
            public Trigger process(Window dialog) {
            	try{
                dialog.getTextBox("groupField").setText("225.1.1.2");
                dialog.getSpinner("portField").setValue(80);
                
                dialog.getComboBox("packetStyleCombo").select("Hirschmann Packet Format");
                dialog.getTextBox("dataField").setText("You suck");
                
                dialog.getSpinner("packetRateField").setValue(999);
                dialog.getSpinner("packetSizeField").setValue(300);
                
                dialog.getSpinner("ttlField").setValue(12);
                dialog.getComboBox("analyzingBehaviourCombo").select("Lazy");
                
                dialog.getCheckBox("activateBox").select();
                
                return dialog.getButton("okButton").triggerClick();
            	} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
            }
        })
        .run();
        
        final File file = new File(
        		System.getProperty("java.io.tmpdir")+
        		File.separator + "mc_test_profile");
        
        // Create Profile
        WindowInterceptor
        .init(
        		// The fact that we have to use real English names is retarded
        		// but the framework doesn't support doing it with internal name
        		window.getMenuBar().getMenu("File")
        		.getSubMenu("Save Profile As...").triggerClick()
        )
        .process(new WindowHandler() {
            public Trigger process(Window dialog) throws Exception {
	            dialog.getTextBox("txtProfileName").setText("Test Profile");
            	
                return FileChooserHandler.init()
		       		 .assertIsSaveDialog()
		             .assertAcceptsFilesOnly()
		             .select(file.toString())
		             .process(dialog);
            }
        })
        .run();

        // Restart the application
        ComponentUtils.close(window);
        window = getMainWindow();
        
        // Load Profile
        WindowInterceptor
        .init(
        		window.getMenuBar().getMenu("File")
        		.getSubMenu("Open Profile...").triggerClick()
        )
        .process(FileChooserHandler.init()
		       		 .assertIsOpenDialog()
		             .assertAcceptsFilesOnly()
		             .select(file.toString())
        )
        .run();
        
        Table table = window.getTable("senderTable");
        assertFalse(table.isEmpty());
        
        // Edit the first Element in the table
        table.selectRow(0);
        
        WindowInterceptor.init(window.getButton("buEditSender").triggerClick())
        .process(new WindowHandler() {
            public Trigger process(Window dialog) {
            	assertTrue(dialog.getTextBox("groupField").textEquals("225.1.1.2"));
            	assertTrue(dialog.getSpinner("portField").valueEquals(80));

            	assertTrue(dialog.getComboBox("packetStyleCombo").selectionEquals("Hirschmann Packet Format"));
            	assertTrue(dialog.getTextBox("dataField").textEquals("You suck"));

            	assertTrue(dialog.getSpinner("packetRateField").valueEquals(999));
                assertTrue(dialog.getSpinner("packetSizeField").valueEquals(300));
                
                assertTrue(dialog.getSpinner("ttlField").valueEquals(new Byte((byte)12)));
                assertTrue(dialog.getComboBox("analyzingBehaviourCombo").selectionEquals("Lazy"));

                assertTrue(dialog.getCheckBox("activateBox").isEnabled());
            	
                return dialog.getButton("okButton").triggerClick();
            }
        })
        .run();
    }
}