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
        System.out.println("getRowCount");
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
        System.out.println("getColumnCount");
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
        System.out.println("getValueAt");
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
        System.out.println("receiverGroupAdded");
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
        System.out.println("receiverGroupRemoved");
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
        System.out.println("dataChanged");
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
        System.out.println("expand");
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
        System.out.println("collapse");
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
        System.out.println("mouseClicked");
        MouseEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.mouseClicked(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mousePressed method, of class ReceiverTableModel.
     */
    @Test
    public void testMousePressed() {
        System.out.println("mousePressed");
        MouseEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.mousePressed(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mouseReleased method, of class ReceiverTableModel.
     */
    @Test
    public void testMouseReleased() {
        System.out.println("mouseReleased");
        MouseEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.mouseReleased(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mouseEntered method, of class ReceiverTableModel.
     */
    @Test
    public void testMouseEntered() {
        System.out.println("mouseEntered");
        MouseEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.mouseEntered(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mouseExited method, of class ReceiverTableModel.
     */
    @Test
    public void testMouseExited() {
        System.out.println("mouseExited");
        MouseEvent e = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.mouseExited(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReceiverGroupAt method, of class ReceiverTableModel.
     */
    @Test
    public void testGetReceiverGroupAt() {
        System.out.println("getReceiverGroupAt");
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
        System.out.println("getReceiverAt");
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
        System.out.println("getParent");
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
        System.out.println("receiverRemoved");
        Receiver r = null;
        ReceiverTableModel instance = new ReceiverTableModel();
        instance.receiverRemoved(r);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}