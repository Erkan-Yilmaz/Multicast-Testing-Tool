package com.spam.mctool.view.main.sendertable;

import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderDataChangedEvent;
import com.spam.mctool.model.Sender;
import com.spam.mctool.view.main.StatusRenderer;
import com.spam.mctool.view.main.TwoColorRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * Specializes a JTable on representation of Senders in the view. By default
 * uses a SenderTableModel as data model and delegates its Sender management
 * convenience methods. Also acts as its own listener of user events.
 *
 * @author Tobias Stöckel
 */
public class JSenderTable extends JTable {

    /**
     * Initializes the table with a SenderTableModel by default.
     */
    public JSenderTable() {
        init();
        
    }

    /**
     * Sets default SenderTableModel and configures the visual representation
     */
    private void init() {
        this.setModel(new SenderTableModel());
        setGridColor(javax.swing.UIManager.getDefaults().getColor("control"));
        setShowHorizontalLines(false);
        getColumn("Status").setCellRenderer(new StatusRenderer());
        this.setDefaultRenderer(Object.class, new TwoColorRenderer());
        // TODO ...
    }

    /**
     * Forwards the addition of a new Sender to the table model. Throws an
     * IllegalStateException if this JSenderTable's model is no subtype of
     * SenderTableModel.
     *
     * @param s The Sender to be added to the table.
     */
    public void senderAdded(SenderAddedOrRemovedEvent e) {
        try {
            ((SenderTableModel)getModel()).senderAdded(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JSenderTable was initialized with " + getModel().getClass() + " as model.");
        }
    }

    /**
     * Forwards the removal of a sender from the table to the model. Throws an
     * IllegalStateException if this JSenderTable's model is no subtype of
     * SenderTableModel.
     *
     * @param s The Sender to be removed from the table.
     */
    public void senderRemoved(SenderAddedOrRemovedEvent e) {
        try {
            ((SenderTableModel)getModel()).senderRemoved(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JSenderTable was initialized with " + getModel().getClass() + " as model.");
        }
    }

    /**
     * Forwards an update of the specified Sender to the JSenderTable's model.
     * Throws an IllegalStateException if this JSenderTable's model is no
     * subtype of SenderTableModel.
     * 
     * @param s
     */
    public void dataChanged(SenderDataChangedEvent e) {
        try {
            ((SenderTableModel)getModel()).dataChanged(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JSenderTable was initialized with " + getModel().getClass() + " as model.");
        }
    }

    public Iterable<Sender> getSelectedSenders() {
        List<Sender> selectedSenders = new ArrayList<Sender>();
        for(int i=getSelectedRow(); i<getSelectedRow() + getSelectedRowCount(); i++) {
            try {
                selectedSenders.add(((SenderTableModel)getModel()).getSenderAt(i));
            } catch (ClassCastException e) {
                throw new IllegalStateException ("JSenderTable initilized with " + getModel().getClass() + " which is not compatible with SenderTableModel.");
            }
        }
        return selectedSenders;
    }
    
    

}
