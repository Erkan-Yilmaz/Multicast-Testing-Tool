/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.sendertable;

import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tobias
 */
public class JSenderTableTest {

    public JSenderTableTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of senderAdded method, of class JSenderTable.
     */
    @Test
    public void testSenderAdded() {
        System.out.println("senderAdded");
        SenderAddedOrRemovedEvent e = null;
        JSenderTable instance = new JSenderTable();
        instance.senderAdded(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of senderRemoved method, of class JSenderTable.
     */
    @Test
    public void testSenderRemoved() {
        System.out.println("senderRemoved");
        SenderAddedOrRemovedEvent e = null;
        JSenderTable instance = new JSenderTable();
        instance.senderRemoved(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dataChanged method, of class JSenderTable.
     */
    @Test
    public void testDataChanged() {
        System.out.println("dataChanged");
        SenderDataChangedEvent e = null;
        JSenderTable instance = new JSenderTable();
        instance.dataChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSelectedSenders method, of class JSenderTable.
     */
    @Test
    public void testGetSelectedSenders() {
        System.out.println("getSelectedSenders");
        JSenderTable instance = new JSenderTable();
        List expResult = null;
        List result = instance.getSelectedSenders();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}