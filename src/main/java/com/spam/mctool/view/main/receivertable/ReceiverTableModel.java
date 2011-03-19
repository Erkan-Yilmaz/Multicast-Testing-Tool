/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tobias Stöckel
 */
public class ReceiverTableModel extends AbstractTableModel implements MouseListener {

    private List<ReceiverTableRow> visibleRows = new ArrayList<ReceiverTableRow>();

    public int getRowCount() {
        return visibleRows.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if(visibleRows.get(rowIndex) instanceof ReceiverGroupRow) {
            ReceiverGroupRow row = (ReceiverGroupRow)visibleRows.get(rowIndex);
            return row;
        } else if (visibleRows.get(rowIndex) instanceof ReceiverRow) {
            ReceiverRow row = (ReceiverRow)visibleRows.get(rowIndex);
            switch(columnIndex) {
                case 0: return row.getReceiver().isAlive();
                case 1: return row.getReceiver().getSenderId();
                case 2: return row.getReceiver().getAvgPPS();
                default:
                    throw new RuntimeException("Illegal columnIndex: " + columnIndex);
            }
        } else {
            throw new RuntimeException("Rowtype not supported!");
        }
    }

    void addReceiverGroup(ReceiverGroup group) {
        int newRowIndex = visibleRows.size();
        visibleRows.add(new ReceiverGroupRow(group));
        this.fireTableRowsInserted(newRowIndex, newRowIndex);
    }

    void addReceiver(ReceiverGroup group, Receiver rcv) {
        int groupRowIndex = getRowIndex(group);
        ReceiverGroupRow groupRow = (ReceiverGroupRow)visibleRows.get(groupRowIndex);
        ReceiverRow receiverRow = new ReceiverRow(rcv);
        groupRow.addReceiverRow(receiverRow);
        if(groupRow.isExpanded()) {
            int receiverRowIndex = groupRowIndex+1;
            visibleRows.add(receiverRowIndex, receiverRow);
            this.fireTableRowsInserted(receiverRowIndex, receiverRowIndex);
        }
    }

    private int getRowIndex(Object rowContent) {
        for(int i=0; i<visibleRows.size(); i++) {
            if(visibleRows.get(i) instanceof ReceiverGroupRow) {
                ReceiverGroupRow row = (ReceiverGroupRow)visibleRows.get(i);
                if(row.getReceiverGroup() == rowContent) return i;
            } else if(visibleRows.get(i) instanceof ReceiverRow) {
                ReceiverRow row = (ReceiverRow)visibleRows.get(i);
                if(row.getReceiver() == rowContent) return i;
            }
        }
        return -1;
    }

    void expand(int groupRowIndex) {
        ReceiverGroupRow groupRow = (ReceiverGroupRow)visibleRows.get(groupRowIndex);
        if(!groupRow.isExpanded()) {
            int newRowIndex = groupRowIndex;
            for(ReceiverRow receiverRow : groupRow.getReceiverRows()) {
                newRowIndex++;
                visibleRows.add(newRowIndex, receiverRow);
            }
            groupRow.setExpanded(true);
            this.fireTableRowsUpdated(groupRowIndex, groupRowIndex);
            this.fireTableRowsInserted(groupRowIndex+1, newRowIndex);
        }
    }

    void collapse(int groupRowIndex) {
        ReceiverGroupRow groupRow = (ReceiverGroupRow)visibleRows.get(groupRowIndex);
        if(groupRow.isExpanded()) {
            int rcvRowCount = 0;
            for(ReceiverRow rcvRow : groupRow.getReceiverRows()) {
                visibleRows.remove(rcvRow);
                rcvRowCount++;
            }
            groupRow.setExpanded(false);
            this.fireTableRowsUpdated(groupRowIndex, groupRowIndex);
            this.fireTableRowsDeleted(groupRowIndex+1, groupRowIndex+1+rcvRowCount);
        }
    }

    void updateReceiverGroup(ReceiverGroup group) {
        int groupRowIndex = getRowIndex(group);
        this.fireTableRowsUpdated(groupRowIndex, groupRowIndex);
    }

    void updateReceiver(ReceiverGroup group, Receiver rcv) {
        int groupRowIndex = getRowIndex(group);
        ReceiverGroupRow groupRow = (ReceiverGroupRow)visibleRows.get(groupRowIndex);
        if(groupRow.isExpanded()) {
            int rcvRowIndex = getRowIndex(rcv);
            this.fireTableRowsUpdated(rcvRowIndex, rcvRowIndex);
        }
    }

    public void mouseClicked(MouseEvent e) {
        int rowIndex = ((JTable)e.getSource()).getSelectedRow();
        if(getValueAt(rowIndex,0) instanceof ReceiverGroupRow) {
            ReceiverGroupRow groupRow = (ReceiverGroupRow)getValueAt(rowIndex,0);
            if(groupRow.isExpanded()) {
                collapse(rowIndex);
            } else {
                expand(rowIndex);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    private boolean contains(Object e) {
        if(getRowIndex(e) > -1) return true;
        else                    return false;
    }

    public void receiverDataChanged(ReceiverDataChangedEvent e) {
        ReceiverGroup group = (ReceiverGroup)e.getSource();
        List<Receiver> receivers = e.getReceiverList();
        if(this.contains(group)) {
            updateReceiverGroup(group);
        } else {
            addReceiverGroup(group);
        }
        for(Receiver rcv : receivers) {
            if(this.contains(rcv)) {
                updateReceiver(group, rcv);
            } else {
                addReceiver(group, rcv);
            }
        }
    }

}
