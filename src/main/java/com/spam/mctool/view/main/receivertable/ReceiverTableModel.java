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
        return 5;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if(visibleRows.get(rowIndex) instanceof ReceiverGroupRow) {
            ReceiverGroupRow row = (ReceiverGroupRow)visibleRows.get(rowIndex);
            return row;
        } else if (visibleRows.get(rowIndex) instanceof ReceiverRow) {
            ReceiverRow row = (ReceiverRow)visibleRows.get(rowIndex);
            Receiver receiver = row.getReceiver();
            switch(columnIndex) {
                case 0: return receiver.isAlive();
                case 1: return receiver.getSenderId();
                case 2: return receiver.getSenderConfiguredPPS();
                case 3: return receiver.getAvgPPS();
                case 4: return receiver.getLostPackets();
                default:
                    throw new RuntimeException("Illegal columnIndex: " + columnIndex);
            }
        } else {
            throw new RuntimeException("Rowtype not supported!");
        }
    }

    public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
        ReceiverGroup group = e.getSource();
        int newRowIndex = visibleRows.size();
        visibleRows.add(new ReceiverGroupRow(group));
        this.fireTableRowsInserted(newRowIndex, newRowIndex);
    }

    void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
        ReceiverGroup group = e.getSource();
        ReceiverGroupRow groupRow = getReceiverGroupRow(group);
        int firstRow = getRowIndex(group);
        int lastRow  = firstRow;
        if(groupRow.isExpanded()) {
            for(ReceiverRow row : groupRow.getReceiverRows()) {
                visibleRows.remove(row);
                lastRow++;
            }
        }
        visibleRows.remove(groupRow);
        fireTableRowsDeleted(firstRow, lastRow);
    }

    public void dataChanged(ReceiverDataChangedEvent e) {
        ReceiverGroup receiverGroup = e.getSource();
        ReceiverGroupRow receiverGroupRow = getReceiverGroupRow(receiverGroup);
        int groupRowIndex = getRowIndex(receiverGroup);
        fireTableRowsUpdated(groupRowIndex, groupRowIndex);

        ReceiverRow receiverRow;
        int receiverRowIndex;
        int insertPos;
        for(Receiver receiver : e.getReceiverList()) {
            if(contains(receiver)) {
                if (receiverGroupRow.isExpanded()) {
                    receiverRowIndex = this.getRowIndex(receiver);
                    fireTableRowsUpdated(receiverRowIndex, receiverRowIndex);
                }
            } else {
                receiverRow = new ReceiverRow(receiver);
                receiverGroupRow.addReceiverRow(receiverRow);
                if(receiverGroupRow.isExpanded()) {
                   insertPos = groupRowIndex + receiverGroupRow.getReceiverRowCount() + 1;
                   visibleRows.add(insertPos, receiverRow);
                   fireTableRowsInserted(insertPos, insertPos);
                }
            }
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

    private ReceiverGroupRow getReceiverGroupRow(ReceiverGroup receiverGroup) {
        for(ReceiverTableRow row : visibleRows) {
            if(row instanceof ReceiverGroupRow) {
                ReceiverGroupRow groupRow = (ReceiverGroupRow)row;
                if(groupRow.getReceiverGroup() == receiverGroup) {
                    return groupRow;
                }
            }
        }
        return null;
    }

    private ReceiverRow getReceiverRow(Receiver receiver) {
        for(ReceiverTableRow row : visibleRows) {
            if(row instanceof ReceiverRow) {
                ReceiverRow receiverRow = (ReceiverRow)row;
                if(receiverRow.getReceiver() == receiver) {
                    return receiverRow;
                }
            }
        }
        return null;
    }

    private boolean contains(Receiver receiver) {
        for(ReceiverTableRow row : visibleRows) {
            if(row instanceof ReceiverGroupRow) {
                ReceiverGroupRow groupRow = (ReceiverGroupRow)row;
                for(ReceiverRow receiverRow : groupRow.getReceiverRows()) {
                    if(receiverRow.getReceiver() == receiver) return true;
                }
            }
        }
        return false;
    }

}
