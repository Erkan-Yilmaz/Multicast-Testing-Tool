/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import java.awt.event.MouseEvent;
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
public class ReceiverTableModelTest {

    public ReceiverTableModelTest() {
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
     * Test of getRowCount method, of class ReceiverTableModel.
     */
    @Test
    public void testGetRowCount() {
        ReceiverTableModel instance = new ReceiverTableModel();
        int expResult = 0;
        int result = instance.getRowCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnCount method, of class ReceiverTableModel.
     */
    @Test
    public void testGetColumnCount() {
        ReceiverTableModel instance = new ReceiverTableModel();
        int expResult = 0;
        int result = instance.getColumnCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValueAt method, of class ReceiverTableModel.
     */
    @Test
    public void testGetValueAt() {
        int rowIndex = 0;
        int columnIndex = 0;
        ReceiverTableModel instance = new ReceiverTableModel();
        Object expResult = null;
        Object result = instance.getValueAt(rowIndex, columnIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of receiverGroupAdded method, of class ReceiverTableModel.
     */
    @Test
    public void testReceiverGroupAdded() {
        ReceiverAddedOrRemovedEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.receiverGroupAdded(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of receiverGroupRemoved method, of class ReceiverTableModel.
     */
    @Test
    public void testReceiverGroupRemoved() {
        ReceiverAddedOrRemovedEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.receiverGroupRemoved(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dataChanged method, of class ReceiverTableModel.
     */
    @Test
    public void testDataChanged() {
        ReceiverDataChangedEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.dataChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of expand method, of class ReceiverTableModel.
     */
    @Test
    public void testExpand() {
        int visGroupRowIndex = 0;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.expand(visGroupRowIndex);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of collapse method, of class ReceiverTableModel.
     */
    @Test
    public void testCollapse() {
        int visGroupRowIndex = 0;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.collapse(visGroupRowIndex);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mouseClicked method, of class ReceiverTableModel.
     */
    @Test
    public void testMouseClicked() {
        MouseEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.mouseClicked(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReceiverGroupAt method, of class ReceiverTableModel.
     */
    @Test
    public void testGetReceiverGroupAt() {
        int rowIndex = 0;
        ReceiverTableModel instance = new ReceiverTableModel();
        ReceiverGroup expResult = null;
        ReceiverGroup result = instance.getReceiverGroupAt(rowIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReceiverAt method, of class ReceiverTableModel.
     */
    @Test
    public void testGetReceiverAt() {
        int rowIndex = 0;
        ReceiverTableModel instance = new ReceiverTableModel();
        Receiver expResult = null;
        Receiver result = instance.getReceiverAt(rowIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class ReceiverTableModel.
     */
    @Test
    public void testGetParent() {
        Receiver r = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        ReceiverGroup expResult = null;
        ReceiverGroup result = instance.getParent(r);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of receiverRemoved method, of class ReceiverTableModel.
     */
    @Test
    public void testReceiverRemoved() {
        Receiver r = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.receiverRemoved(r);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}