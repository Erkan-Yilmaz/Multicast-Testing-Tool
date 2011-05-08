/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.sendertable;

import java.net.InetAddress;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.Sender;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author Tobias
 */
public class SenderTableModelTest {

    public SenderTableModelTest() {
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
     * Test of senderAdded method, of class SenderTableModel.
     */
    @Test
    public void testSenderAdded() throws Exception {

        //----------------------- Add a first sender ---------------------------

        // Create a sender addition event containing a mocked sender
        Sender mockedSender = mock(Sender.class);
        when(mockedSender.getSenderId()).thenReturn(667788);
        when(mockedSender.getPort()).thenReturn(12345);
        when(mockedSender.getGroup()).thenReturn(InetAddress.getByName("225.1.1.14"));
        when(mockedSender.getSenderConfiguredPacketRate()).thenReturn(200);
        when(mockedSender.getAvgPPS()).thenReturn(30l);
        when(mockedSender.getMinPPS()).thenReturn(150l);
        when(mockedSender.getMaxPPS()).thenReturn(400l);

        SenderAddedOrRemovedEvent e = new SenderAddedOrRemovedEvent(mockedSender);

        // create a SenderTableModel and feed it with the mocked sender
        SenderTableModel instance = new SenderTableModel();
        instance.senderAdded(e);

        // construct a vector containing one row (another vector) of data to
        // check the table model's internal one against
        Vector expectedRow = new Vector();
        expectedRow.add(mockedSender);
        expectedRow.add(667788);
        expectedRow.add(12345);
        expectedRow.add("225.1.1.14");
        expectedRow.add(200);
        expectedRow.add(30l);
        expectedRow.add(150l);
        expectedRow.add(400l);

        Vector expected = new Vector();
        expected.add(expectedRow);

        // check the table model's internal data vector for correct insertion
        // of the sender's values
        Vector actual = Whitebox.getInternalState(instance, "dataVector");
        assertEquals(expected, actual);

        //------------------------ Add a second sender -------------------------

        // Create a sender addition event containing a mocked sender
        mockedSender = mock(Sender.class);
        when(mockedSender.getSenderId()).thenReturn(778899);
        when(mockedSender.getPort()).thenReturn(12346);
        when(mockedSender.getGroup()).thenReturn(InetAddress.getByName("225.1.1.15"));
        when(mockedSender.getSenderConfiguredPacketRate()).thenReturn(1000);
        when(mockedSender.getAvgPPS()).thenReturn(999l);
        when(mockedSender.getMinPPS()).thenReturn(123l);
        when(mockedSender.getMaxPPS()).thenReturn(1001l);

        e = new SenderAddedOrRemovedEvent(mockedSender);

        // feed the existing table model instance with another sender addition
        // event containing a newly mocked sender.
        instance.senderAdded(e);

        // construct a new row of data and add it to the existing expectation
        // vector
        expectedRow = new Vector();
        expectedRow.add(mockedSender);
        expectedRow.add(778899);
        expectedRow.add(12346);
        expectedRow.add("225.1.1.15");
        expectedRow.add(1000);
        expectedRow.add(999l);
        expectedRow.add(123l);
        expectedRow.add(1001l);

        expected.add(expectedRow);

        // check the table model's internal data vector for correct insertion
        // of the second sender's values
        actual = Whitebox.getInternalState(instance, "dataVector");
        assertEquals(expected, actual);

        //--------------------- Add the same sender again ----------------------

        // Create a sender addition event containing the above sender once again
        e = new SenderAddedOrRemovedEvent(mockedSender);

        // feed the existing table model instance with another sender addition
        // event containg a previously added sender. Should cause an Exception
        try {
            instance.senderAdded(e);
            fail("Sender successfully added twice.");
        } catch (Exception ex) {
            assertTrue(ex instanceof RuntimeException);
        }
    }

    /**
     * Test of dataChanged method, of class SenderTableModel.
     */
    @Test
    public void testDataChanged() {
        System.out.println("dataChanged");
        SenderDataChangedEvent e = null;
        SenderTableModel instance = new SenderTableModel();
        instance.dataChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of senderRemoved method, of class SenderTableModel.
     */
    @Test
    public void testSenderRemoved() {
        System.out.println("senderRemoved");
        SenderAddedOrRemovedEvent e = null;
        SenderTableModel instance = new SenderTableModel();
        instance.senderRemoved(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSenderAt method, of class SenderTableModel.
     */
    @Test
    public void testGetSenderAt() {
        System.out.println("getSenderAt");
        int i = 0;
        SenderTableModel instance = new SenderTableModel();
        Sender expResult = null;
        Sender result = instance.getSenderAt(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isCellEditable method, of class SenderTableModel.
     */
    @Test
    public void testIsCellEditable() {
        System.out.println("isCellEditable");
        int row = 0;
        int column = 0;
        SenderTableModel instance = new SenderTableModel();
        boolean expResult = false;
        boolean result = instance.isCellEditable(row, column);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}