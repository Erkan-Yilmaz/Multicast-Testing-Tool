/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import java.util.List;
import javax.swing.table.TableCellRenderer;
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
public class JReceiverTableTest {

    public JReceiverTableTest() {
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
     * Test of getCellRenderer method, of class JReceiverTable.
     */
    @Test
    public void testGetCellRenderer() {
        System.out.println("getCellRenderer");
        int row = 0;
        int column = 0;
        JReceiverTable instance = new JReceiverTable();
        TableCellRenderer expResult = null;
        TableCellRenderer result = instance.getCellRenderer(row, column);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of receiverGroupAdded method, of class JReceiverTable.
     */
    @Test
    public void testReceiverGroupAdded() {
        System.out.println("receiverGroupAdded");
        ReceiverAddedOrRemovedEvent e = null;
        JReceiverTable instance = new JReceiverTable();
        instance.receiverGroupAdded(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of receiverGroupRemoved method, of class JReceiverTable.
     */
    @Test
    public void testReceiverGroupRemoved() {
        System.out.println("receiverGroupRemoved");
        ReceiverAddedOrRemovedEvent e = null;
        JReceiverTable instance = new JReceiverTable();
        instance.receiverGroupRemoved(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dataChanged method, of class JReceiverTable.
     */
    @Test
    public void testDataChanged() {
        System.out.println("dataChanged");
        ReceiverDataChangedEvent e = null;
        JReceiverTable instance = new JReceiverTable();
        instance.dataChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSelectedReceiverGroups method, of class JReceiverTable.
     */
    @Test
    public void testGetSelectedReceiverGroups() {
        System.out.println("getSelectedReceiverGroups");
        JReceiverTable instance = new JReceiverTable();
        List expResult = null;
        List result = instance.getSelectedReceiverGroups();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSelectedReceivers method, of class JReceiverTable.
     */
    @Test
    public void testGetSelectedReceivers() {
        System.out.println("getSelectedReceivers");
        JReceiverTable instance = new JReceiverTable();
        List expResult = null;
        List result = instance.getSelectedReceivers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class JReceiverTable.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        Receiver r = null;
        JReceiverTable instance = new JReceiverTable();
        ReceiverGroup expResult = null;
        ReceiverGroup result = instance.getParent(r);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of receiverRemoved method, of class JReceiverTable.
     */
    @Test
    public void testReceiverRemoved() {
        System.out.println("receiverRemoved");
        Receiver r = null;
        JReceiverTable instance = new JReceiverTable();
        instance.receiverRemoved(r);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}