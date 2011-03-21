package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.view.main.TwoColorRenderer;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Tobias Stöckel
 */
public class JReceiverTable extends JTable {

    private final GroupRowRenderer groupRowRenderer = new GroupRowRenderer();

    public JReceiverTable() {
        this(new ReceiverTableModel());
    }

    public JReceiverTable(ReceiverTableModel model) {
        super(model);
        this.init();
    }

    private void init() {
        this.getColumnModel().getColumn(0).setHeaderValue("Status");
        this.getColumnModel().getColumn(1).setHeaderValue("Sender ID");
        this.getColumnModel().getColumn(2).setHeaderValue("Conf. PPS");
        this.getColumnModel().getColumn(3).setHeaderValue("Measured PPS");
        this.getColumnModel().getColumn(4).setHeaderValue("Lost Packets");
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

}
