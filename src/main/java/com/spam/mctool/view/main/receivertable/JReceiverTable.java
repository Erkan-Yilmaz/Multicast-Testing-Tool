package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.view.main.TwoColorRenderer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Specializes a JTable for representing ReceiverGroups and Receivers.
 * A JReceiverTable presents each ReceiverGroup as one row and a collapsible
 * set of subrows that represent the Receivers associated with this ReceiverGroup.
 * JReceiverTable is tightly coupled with the ReceiverTableModel and will throw
 * IllegalStateExceptions when trying to access ReceiverTable specific actions
 * while its model is no subtype of ReceiverTableModel.
 *
 * @author Tobias Stöckel
 */
public class JReceiverTable extends JTable {

    /**
     * The renderer that will be used for rendering a row representing a
     * ReceiverGroup.
     */
    private final GroupRowRenderer groupRowRenderer = new GroupRowRenderer();

    public JReceiverTable() {
        this(new ReceiverTableModel());
    }

    public JReceiverTable(ReceiverTableModel model) {
        super(model);
        this.init();
    }

    private void init() {
        ReceiverTableColumn[] cols = ReceiverTableColumn.values();
        for(int i=0; i<cols.length; i++) {
            this.getColumnModel().getColumn(i).setHeaderValue(cols[i].getCaption());
        }
        this.getColumnModel().getColumn(0).setCellRenderer(new ReceiverStateRenderer());
        this.addMouseListener((ReceiverTableModel)this.getModel());
        this.setDefaultRenderer(Object.class, new TwoColorRenderer());
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(getValueAt(row,column) instanceof ReceiverGroupRow) {
            return groupRowRenderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
        try {
            ((ReceiverTableModel)getModel()).receiverGroupAdded(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }

    public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
        try {
            ((ReceiverTableModel)getModel()).receiverGroupRemoved(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }

    public void dataChanged(ReceiverDataChangedEvent e) {
        try {
            ((ReceiverTableModel)getModel()).dataChanged(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }

    /**
     * Returns the ReceiverGroups whose rows are currently selected. Returns
     * an empty list, if no row is selected or if the selected rows only contain
     * Receivers.
     * @return List of selected ReceiverGroups, or the empty list if there are
     *         none.
     */
    public List<ReceiverGroup> getSelectedReceiverGroups() {
        Set<ReceiverGroup> groupSet = new HashSet<ReceiverGroup>();
        if(dataModel instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)dataModel;
            for(int i : getSelectedRows()) {
                ReceiverGroup g = model.getReceiverGroupAt(i);
                if(g != null) {
                    groupSet.add(g);
                }
            }
            return new ArrayList<ReceiverGroup>(groupSet);
        } else {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }

    /**
     * Returns the Receivers whose rows are currently selected. Returns an empty
     * list, if no row is selected or if the selected rows only contain ReceiverGroups.
     * @return List of selected Receivers, or the empty list if there are none.
     */
    public List<Receiver> getSelectedReceivers() {
        List<Receiver> receiverList = new ArrayList<Receiver>();
        if(dataModel instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)dataModel;
            for(int i : getSelectedRows()) {
                Receiver rcv = model.getReceiverAt(i);
                // if a null is returned, this row contained a ReceiverGroup
                if(rcv != null) {
                    receiverList.add(rcv);
                }
            }
            return receiverList;
        } else {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }

    public ReceiverGroup getParent(Receiver r) {
        if(this.getModel() instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)getModel();
            return model.getParent(r);
        } else {
            throw new IllegalStateException("JReceiverTable must contain a ReceiverTableModel!");
        }
    }

    public void receiverRemoved(Receiver r) {
        if(this.getModel() instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)getModel();
            model.receiverRemoved(r);
        } else {
            throw new IllegalStateException("JReceiverTable must contain a ReceiverTableModel!");
        }
    }

}
