/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.sendertable;

import java.net.InetAddress;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.Sender;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;
import static javax.swing.event.TableModelEvent.*;

/**
 *
 * @author Tobias
 */
public class SenderTableModelTest {
    private final int SENDER_COUNT = 5;

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
     * Test of senderAdded method, of class SenderTableModel. Also checks if
     * the correct events are generated for the displaying JSenderTable.
     */
    @Test
    public void testSenderAdded() throws Exception {

        //----------------------- Add a first sender ---------------------------

        // Create a mocked table model listener to intercept table model events
        TableModelListener mockedListener        = mock(TableModelListener.class);
        ArgumentCaptor<TableModelEvent> argument = ArgumentCaptor.forClass(TableModelEvent.class);
        TableModelEvent capturedEvent;

        // Create a sender addition event containing a mocked sender
        Sender mockedSender = mock(Sender.class);
        when(mockedSender.getSenderId()).thenReturn(667788);
        when(mockedSender.getPort())    .thenReturn(12345);
        when(mockedSender.getGroup())   .thenReturn(InetAddress.getByName("225.1.1.14"));
        when(mockedSender.getSenderConfiguredPacketRate()).thenReturn(200);
        when(mockedSender.getAvgPPS())  .thenReturn(30l);
        when(mockedSender.getMinPPS())  .thenReturn(150l);
        when(mockedSender.getMaxPPS())  .thenReturn(400l);

        SenderAddedOrRemovedEvent e = new SenderAddedOrRemovedEvent(mockedSender);

        // create a SenderTableModel and feed it with the mocked sender
        SenderTableModel instance = new SenderTableModel();
        instance.addTableModelListener(mockedListener);
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
        Vector actual = instance.getDataVector();
        assertEquals(expected, actual);

        // check if the correct table event was issued
        verify(mockedListener).tableChanged(argument.capture());
        capturedEvent = argument.getValue();
        assertEquals(instance,    capturedEvent.getSource()  );
        assertEquals(INSERT,      capturedEvent.getType()    );
        assertEquals(ALL_COLUMNS, capturedEvent.getColumn()  );
        assertEquals(0,           capturedEvent.getFirstRow());
        assertEquals(0,           capturedEvent.getLastRow() );

        //------------------------ Add a second sender -------------------------

        // Create a sender addition event containing a mocked sender
        mockedSender = mock(Sender.class);
        when(mockedSender.getSenderId()) .thenReturn(778899);
        when(mockedSender.getPort())     .thenReturn(12346);
        when(mockedSender.getGroup())    .thenReturn(InetAddress.getByName("225.1.1.15"));
        when(mockedSender.getSenderConfiguredPacketRate()).thenReturn(1000);
        when(mockedSender.getAvgPPS())   .thenReturn(999l);
        when(mockedSender.getMinPPS())   .thenReturn(123l);
        when(mockedSender.getMaxPPS())   .thenReturn(1001l);

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
        actual = instance.getDataVector();
        assertEquals(expected, actual);

        // check if the correct table event was issued
        verify(mockedListener, times(2)).tableChanged(argument.capture());
        capturedEvent = argument.getValue();
        assertEquals(instance,    capturedEvent.getSource());
        assertEquals(INSERT,      capturedEvent.getType());
        assertEquals(ALL_COLUMNS, capturedEvent.getColumn());
        assertEquals(1,           capturedEvent.getFirstRow());
        assertEquals(1,           capturedEvent.getLastRow());

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

        // check that the sender was not added to the model. The data vector
        // still has to equal the one from the previous test.
        actual = instance.getDataVector();
        assertEquals(expected, actual);

        // check that no additional table event was issued
        verify(mockedListener, times(2)).tableChanged(argument.capture());
    }

    /**
     * Test of dataChanged method, of class SenderTableModel.
     */
    @Test
    public void testDataChanged() throws Exception {
        
        SenderTableModel instance = new SenderTableModel();

        // Generate a few mocked senders and according sender removal events
        List<Sender> mockedSenders = new ArrayList<Sender>();
        List<SenderAddedOrRemovedEvent> events = new ArrayList<SenderAddedOrRemovedEvent>();
        Sender mockedSender;
        for(int i=0; i<SENDER_COUNT; i++) {
            mockedSender = mock(Sender.class);
            when(mockedSender.getSenderId()) .thenReturn(667788 + i);
            when(mockedSender.getPort())     .thenReturn(12345 + i);
            when(mockedSender.getGroup())    .thenReturn(InetAddress.getByName("225.1.1." + (i)));
            when(mockedSender.getSenderConfiguredPacketRate()).thenReturn(100*i);
            when(mockedSender.getAvgPPS())   .thenReturn(89l*i);
            when(mockedSender.getMinPPS())   .thenReturn(10l*i);
            when(mockedSender.getMaxPPS())   .thenReturn(105l*i);
            mockedSenders.add(mockedSender);
            events.add(new SenderAddedOrRemovedEvent(mockedSender));
        }

        //----------------------- Remove a single Sender -----------------------

        // Add a single sender to the table model
        instance.senderAdded(events.get(0));

        // Remove it again
        instance.senderRemoved(events.get(0));

        // Check removal
        Vector expected = new Vector();
        Vector actual   = instance.getDataVector();
        assertEquals(expected, actual);
        assertEquals(0l, instance.getRowCount());

        //----------------------- Remove the last sender -----------------------

        // Add a few senders to the table model
        instance.senderAdded(events.get(0));
        instance.senderAdded(events.get(1));
        instance.senderAdded(events.get(2));
        instance.senderAdded(events.get(3));

        // Remove the last one from the model
        instance.senderRemoved(events.get(3));

        // Construct the expected data vector
        expected = new Vector();
        Vector expectedRow;
        for(int i=0; i<3; i++) {
            expectedRow = new Vector();
            expectedRow.add(mockedSenders.get(i));
            expectedRow.add(667788+i);
            expectedRow.add(12345+i);
            expectedRow.add("225.1.1." + (i));
            expectedRow.add(100*i);
            expectedRow.add(89l*i);
            expectedRow.add(10l*i);
            expectedRow.add(105l*i);
            expected.add(expectedRow);
        }
        actual = instance.getDataVector();
        assertEquals(expected, actual);

        //---------------------- Remove the first sender -----------------------

        // remove the first sender from the above model
        instance.senderRemoved(events.get(0));

        // construct the expected data vector
        expected = new Vector();
        for(int i=1; i<3; i++) {
            expectedRow = new Vector();
            expectedRow.add(mockedSenders.get(i));
            expectedRow.add(667788+i);
            expectedRow.add(12345+i);
            expectedRow.add("225.1.1." + (i));
            expectedRow.add(100*i);
            expectedRow.add(89l*i);
            expectedRow.add(10l*i);
            expectedRow.add(105l*i);
            expected.add(expectedRow);
        }
        actual = instance.getDataVector();
        assertEquals(expected, actual);

        //------------------- Remove a sender not in the list ------------------

        // this case should throw an exception
        try {
            instance.senderRemoved(events.get(4));
        } catch (Exception ex) {
            assertTrue(ex instanceof RuntimeException);
        }


    }

    /**
     * Test of senderRemoved method, of class SenderTableModel. Also checks if
     * the correct events are generated for the displaying JSenderTable
     */
    @Test
    public void testSenderRemoved() throws Exception {
        SenderTableModel instance = new SenderTableModel();

        // Generate a few mocked senders and according sender removal events
        List<Sender> mockedSenders = new ArrayList<Sender>();
        List<SenderAddedOrRemovedEvent> events = new ArrayList<SenderAddedOrRemovedEvent>();
        Sender mockedSender;
        for(int i=0; i<SENDER_COUNT; i++) {
            mockedSender = mock(Sender.class);
            when(mockedSender.getSenderId()) .thenReturn(667788 + i);
            when(mockedSender.getPort())     .thenReturn(12345 + i);
            when(mockedSender.getGroup())    .thenReturn(InetAddress.getByName("225.1.1." + (i)));
            when(mockedSender.getSenderConfiguredPacketRate()).thenReturn(100*i);
            when(mockedSender.getAvgPPS())   .thenReturn(89l*i);
            when(mockedSender.getMinPPS())   .thenReturn(10l*i);
            when(mockedSender.getMaxPPS())   .thenReturn(105l*i);
            mockedSenders.add(mockedSender);
            events.add(new SenderAddedOrRemovedEvent(mockedSender));
        }

        //----------------------- Remove a single Sender -----------------------

        // Add a single sender to the table model
        instance.senderAdded(events.get(0));

        // Remove it again
        instance.senderRemoved(events.get(0));

        // Check removal
        Vector expected = new Vector();
        Vector actual   = instance.getDataVector();
        assertEquals(expected, actual);
        assertEquals(0l, instance.getRowCount());

        //----------------------- Remove the last sender -----------------------

        // Add a few senders to the table model
        instance.senderAdded(events.get(0));
        instance.senderAdded(events.get(1));
        instance.senderAdded(events.get(2));
        instance.senderAdded(events.get(3));

        // Remove the last one from the model
        instance.senderRemoved(events.get(3));

        // Construct the expected data vector
        expected = new Vector();
        Vector expectedRow;
        for(int i=0; i<3; i++) {
            expectedRow = new Vector();
            expectedRow.add(mockedSenders.get(i));
            expectedRow.add(667788+i);
            expectedRow.add(12345+i);
            expectedRow.add("225.1.1." + (i));
            expectedRow.add(100*i);
            expectedRow.add(89l*i);
            expectedRow.add(10l*i);
            expectedRow.add(105l*i);
            expected.add(expectedRow);
        }
        actual = instance.getDataVector();
        assertEquals(expected, actual);

        //---------------------- Remove the first sender -----------------------

        // remove the first sender from the above model
        instance.senderRemoved(events.get(0));

        // construct the expected data vector
        expected = new Vector();
        for(int i=1; i<3; i++) {
            expectedRow = new Vector();
            expectedRow.add(mockedSenders.get(i));
            expectedRow.add(667788+i);
            expectedRow.add(12345+i);
            expectedRow.add("225.1.1." + (i));
            expectedRow.add(100*i);
            expectedRow.add(89l*i);
            expectedRow.add(10l*i);
            expectedRow.add(105l*i);
            expected.add(expectedRow);
        }
        actual = instance.getDataVector();
        assertEquals(expected, actual);

        //------------------- Remove a sender not in the list ------------------

        // this case should throw an exception
        try {
            instance.senderRemoved(events.get(4));
        } catch (Exception ex) {
            assertTrue(ex instanceof RuntimeException);
        }
    }

    /**
     * Test of getSenderAt method, of class SenderTableModel.
     */
    @Test
    public void testGetSenderAt() {
        int i = 0;
        SenderTableModel instance = new SenderTableModel();
        Sender expResult = null;
        Sender result = instance.getSenderAt(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}