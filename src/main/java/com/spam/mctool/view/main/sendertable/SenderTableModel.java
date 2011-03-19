package com.spam.mctool.view.main.sendertable;

import com.spam.mctool.model.Sender;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * An extension to the DefaultTableModel for use with a JTable. The
 * SenderTableModel encapsulates the configuration of a DefaultTableModel
 * specialized on representing Senders. Provides
 * convenience methods for managing the representation of Senders in the view.
 *
 * @author Tobias Stöckel
 */
public class SenderTableModel extends DefaultTableModel {

    /**
     * Constructs an empty SenderTableModel and initializes it with a default
     * set of columns specific to Sender representation.
     */
    public SenderTableModel() {
        super();
        init();
    }

    /**
     * Initializes this SenderTableModel with a set of columns specific to
     * Sender representation.
     */
    private void init() {
        this.addColumn("Status");
        this.addColumn("Sender ID");
        this.addColumn("Port");
        this.addColumn("Multicast Group");
        this.addColumn("Conf. Packet Rate");
        this.addColumn("Avg. Packet Rate");
        this.addColumn("Min. Packet Rate");
        this.addColumn("Max. Packet Rate");
    }

    /**
     * Adds a Sender to this table model. The JTable connected to this model
     * will be updated automatically. Throws a RuntimeException if the Sender
     * to be added, already exists in the table model.
     *
     * @param s The Sender to be newly represented by this table model.
     */
    public void addSender(Sender s) {
        if (!this.contains(s)) {
            this.addRow (
                new Object[] {
                    s,
                    s.getSenderId(),
                    s.getPort(),
                    s.getGroup(),
                    s.getSenderConfiguredPacketRate(),
                    s.getAvgPPS(),
                    s.getMinPPS(),
                    s.getMaxPPS()
                }
            );
        } else {
            throw new RuntimeException("Sender " + s + " already added to view.");
        }
    }

    /**
     * Updates the table with the specified Sender's data. This will generate a
     * TableRowsUpdated event to update the JTable.
     * @param s
     */
    public void updateSender(Sender s) {
        int senderRow = findSenderRow(s);
        if(senderRow > -1) {
            Vector rowVector = (Vector)dataVector.elementAt(senderRow);
            rowVector.setElementAt(s,               0);
            rowVector.setElementAt(s.getSenderId(), 1);
            rowVector.setElementAt(s.getPort(),     2);
            rowVector.setElementAt(s.getGroup(),    3);
            rowVector.setElementAt(s.getSenderConfiguredPacketRate(), 4);
            rowVector.setElementAt(s.getAvgPPS(),   5);
            rowVector.setElementAt(s.getMinPPS(),   6);
            rowVector.setElementAt(s.getMaxPPS(),   7);
            this.fireTableRowsUpdated(senderRow, senderRow);
        } else {
            throw new RuntimeException("Sender " + s + " not found in table model.");
        }
    }
    
    /**
     * Removes a Sender's representation from this table model. Throws a
     * RuntimeException, if the sender e is not found in the table model.
     *
     * @param s The sender to be removed.
     */
    public void removeSender(Sender s) {
        int senderRow = findSenderRow(s);
        if(senderRow > -1) {
            this.removeRow(senderRow);
        } else {
            throw new RuntimeException("Sender " + s + " not in list.");
        }
    }

    /**
     * Searches the representation of a given Sender in this table model. Scans
     * all rows of this model, searching for the given Sender's object reference
     * in the first column. Returns the index of the row that the Sender is
     * represented by.
     * 
     * @param s The Sender to be searched in the model.
     * @return The index of the row in this model containing the representation
     *         of the given Sender.
     */
    private int findSenderRow(Sender s) {
        for(int i=0; i < this.getRowCount(); i++) {
            if(this.getValueAt(i, 0) == s) {
                // sender found in row i
                return i;
            }
        }
        // sender not found. returning -1
        return -1;
    }

    /**
     * Determines, if the given Sender is currently represented by a row of this
     * model. This method calls findSenderRow which returns -1 if no matching
     * row is found.
     *
     * @param s The Sender whose representation is to be tested.
     * @return True, if the Sender is represented by this model. False, if not.
     */
    private boolean contains(Sender s) {
        if(findSenderRow(s) > -1)
            return true;
        else
            return false;
    }

    /**
     * Returns the Sender that is represented by the given row in the model.
     * Returns an IllegalStateException if the first column of this row does
     * not contain a Sender or one of its subtypes.
     * 
     * @param i The row index that is queried for the Sender.
     * @return The Sender that is represented by row i.
     */
    Sender getSenderAt(int i) {
        try {
            return (Sender)getValueAt(i, 0);
        } catch (ClassCastException e) {
            throw new IllegalStateException("The first column of this model must contain a Sender but contains " + getValueAt(i, 0) + ".");
        }
    }

    /**
     * Marks all cells in the table as not editable by default.
     * @param row
     * @param column
     * @return
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
