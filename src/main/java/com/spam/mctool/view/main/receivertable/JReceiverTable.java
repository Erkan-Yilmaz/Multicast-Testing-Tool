package com.spam.mctool.view.main.receivertable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Tobias Stöckel
 */
public class JReceiverTable extends JTable {
    private StateRenderer stateRenderer = new StateRenderer();
    private GroupRowRenderer groupRowRenderer = new GroupRowRenderer();

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
        this.getColumnModel().getColumn(2).setHeaderValue("Packet Rate");
        this.getColumnModel().getColumn(0).setCellRenderer(stateRenderer);
        this.addMouseListener((ReceiverTableModel)this.getModel());
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(getValueAt(row,column) instanceof ReceiverGroupRow) {
            return groupRowRenderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }

}
