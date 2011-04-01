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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tobias Stöckel
 */
public class ReceiverTableModel extends AbstractTableModel implements MouseListener {

    private List<ReceiverTableRow> rows    = new ArrayList<ReceiverTableRow>();
    private int columnCount = 6;

    public int getRowCount() {
        int count = 0;
        for(ReceiverTableRow row : rows) {
            if(row.isVisible()) count++;
        }
        return count;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ReceiverTableRow row = getVisibleRow(rowIndex);
        if(row instanceof ReceiverGroupRow) {
            return (ReceiverGroupRow)row;
        } else if(row instanceof ReceiverRow) {
            ReceiverRow rcvRow = (ReceiverRow)row;
            Receiver receiver = rcvRow.getReceiver();
            switch(columnIndex) {
                case 0: return receiver.isAlive();
                case 1: return receiver.getSenderId();
                case 2: return receiver.getSenderConfiguredPPS();
                case 3: return receiver.getAvgPPS();
                case 4: return receiver.getLostPackets();
                case 5: return receiver.getPayloadAsString();
                default:
                    throw new RuntimeException("Illegal columnIndex: " + columnIndex);
            }
        } else {
            throw new RuntimeException("Illegal Rowtype: " + row.getClass().getName());
        }
    }

    private ReceiverTableRow getVisibleRow(int rowIndex) {
        ReceiverTableRow row;
        int visibleIndex = 0;
        for(int i=0; i<rows.size(); i++) {
            row = rows.get(i);
            if(row.isVisible()) {
                if(visibleIndex == rowIndex) {
                    return row;
                }
                // increment the number of traversed visible rows
                visibleIndex++;
            }
        }
        throw new RuntimeException("Illegal Row Index: " + rowIndex);
    }

    void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
        ReceiverGroup group = e.getSource();
        int newRowIndex = getRowCount();
        rows.add(new ReceiverGroupRow(group, true));
        this.fireTableRowsInserted(newRowIndex, newRowIndex);
    }

    void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
        ReceiverGroup group = e.getSource();
        ReceiverGroupRow groupRow = getRow(group);
        int firstRow = getIndex(group);
        int lastRow  = firstRow;
        for(ReceiverRow row : groupRow.getReceiverRows()) {
            if(row.isVisible()) lastRow++;
            rows.remove(row);
        }
        rows.remove(groupRow);
        fireTableRowsDeleted(firstRow, lastRow);
    }

    void dataChanged(ReceiverDataChangedEvent e) {
        ReceiverGroup    receiverGroup    = e.getSource();
        ReceiverGroupRow receiverGroupRow = getRow(receiverGroup);
        int              groupRowIndex    = getVisibleIndex(receiverGroup);

        // update the row of the receiver group
        fireTableRowsUpdated(groupRowIndex, groupRowIndex);

        int receiverRowIndex;
        for(Receiver receiver : e.getReceiverList()) {
            if(contains(receiver)) {
                if (receiverGroupRow.isExpanded()) {
                    receiverRowIndex = getIndex(receiver);
                    fireTableRowsUpdated(receiverRowIndex, receiverRowIndex);
                }
            } else {
                addReceiver(receiverGroup, receiver);
            }
        }
    }

    private int getIndex(ReceiverGroup group) {
        for(int i=0; i<rows.size(); i++) {
            if(rows.get(i) instanceof ReceiverGroupRow) {
                ReceiverGroupRow row = (ReceiverGroupRow)rows.get(i);
                if(row.getReceiverGroup() == group) return i;
            }
        }
        return -1;
    }

    private int getIndex(Receiver receiver) {
        for(int i=0; i<rows.size(); i++) {
            if(rows.get(i) instanceof ReceiverRow) {
                ReceiverRow row = (ReceiverRow)rows.get(i);
                if(row.getReceiver() == receiver) return i;
            }
        }
        return -1;
    }

    void expand(int visGroupRowIndex) {
        ReceiverGroupRow groupRow = (ReceiverGroupRow)getVisibleRow(visGroupRowIndex);
        if(!groupRow.isExpanded()) {
            int newRowIndex = visGroupRowIndex;
            for(ReceiverRow receiverRow : groupRow.getReceiverRows()) {
                newRowIndex++;
                receiverRow.setVisible(true);
            }
            groupRow.setExpanded(true);
            this.fireTableRowsUpdated(visGroupRowIndex, visGroupRowIndex);
            this.fireTableRowsInserted(visGroupRowIndex+1, newRowIndex);
        } else {
            throw new RuntimeException("GroupRow already expanded: " + visGroupRowIndex);
        }
    }

    void collapse(int visGroupRowIndex) {
        ReceiverGroupRow groupRow = (ReceiverGroupRow)getVisibleRow(visGroupRowIndex);
        if(groupRow.isExpanded()) {
            int rcvRowCount = 0;
            for(ReceiverRow rcvRow : groupRow.getReceiverRows()) {
                rcvRow.setVisible(false);
                rcvRowCount++;
            }
            groupRow.setExpanded(false);
            this.fireTableRowsUpdated(visGroupRowIndex, visGroupRowIndex);
            this.fireTableRowsDeleted(visGroupRowIndex+1, visGroupRowIndex+1+rcvRowCount);
        } else {
            throw new RuntimeException("GroupRow already collapsed: " + visGroupRowIndex);
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

    private ReceiverGroupRow getRow(ReceiverGroup receiverGroup) {
        for(ReceiverTableRow row : rows) {
            if(row instanceof ReceiverGroupRow) {
                ReceiverGroupRow groupRow = (ReceiverGroupRow)row;
                if(groupRow.getReceiverGroup() == receiverGroup) {
                    return groupRow;
                }
            }
        }
        throw new RuntimeException("ReceiverGroup not contained: " + receiverGroup);
    }

    private ReceiverRow getRow(Receiver receiver) {
        for(ReceiverTableRow row : rows) {
            if(row instanceof ReceiverRow) {
                ReceiverRow receiverRow = (ReceiverRow)row;
                if(receiverRow.getReceiver() == receiver) {
                    return receiverRow;
                }
            }
        }
        throw new RuntimeException("Receiver not contained: " + receiver);
    }

    private boolean contains(Receiver receiver) {
        for(ReceiverTableRow row : rows) {
            if(row instanceof ReceiverGroupRow) {
                ReceiverGroupRow groupRow = (ReceiverGroupRow)row;
                for(ReceiverRow receiverRow : groupRow.getReceiverRows()) {
                    if(receiverRow.getReceiver() == receiver) return true;
                }
            }
        }
        return false;
    }

    private void addReceiver(ReceiverGroup group, Receiver receiver) {
        // get input position
        ReceiverGroupRow groupRow = getRow(group);
        int groupRowIndex = rows.indexOf(groupRow);
        int inputPos = groupRowIndex + groupRow.getReceiverRowCount() + 1;
        ReceiverRow rcvRow = new ReceiverRow(receiver);
        groupRow.add(rcvRow);
        rows.add(inputPos, rcvRow);

        if(groupRow.isExpanded()) {
            rcvRow.setVisible(true);
            // get visible input position
            int visInputPos = getVisibleIndex(rcvRow);
            this.fireTableRowsInserted(visInputPos, visInputPos);
        }
    }

    private int getVisibleIndex(ReceiverTableRow searchRow) {
        int visibleIndex = 0;
        for(int i=0; i<rows.size(); i++) {
            ReceiverTableRow row = rows.get(i);
            if(row.isVisible()) {
                if(row == searchRow) return visibleIndex;
                visibleIndex++;
            }
        }
        throw new RuntimeException("ReceiverRow not visible: " + searchRow);
    }

    private int getVisibleIndex(ReceiverGroup receiverGroup) {
        int visibleIndex = 0;
        for(int i=0; i<rows.size(); i++) {
            ReceiverTableRow row = rows.get(i);
            if(row.isVisible()) {
                if(row instanceof ReceiverGroupRow) {
                    ReceiverGroupRow receiverRow = (ReceiverGroupRow)row;
                    if(receiverRow.getReceiverGroup() == receiverGroup) {
                        return visibleIndex;
                    }
                }
                visibleIndex++;
            }
        }
        throw new RuntimeException("ReceiverGroup not contained in any visible row: " + receiverGroup);
    }
    
    /*************************** TEST *********************************/

    /*public static void main(String[] args) throws UnknownHostException {
        ReceiverTableModel model = new ReceiverTableModel();
        model.test();
    }

    private void test() throws UnknownHostException {
        int nrOfGroups = 3;
        int rcvsPerGroup = 2;
        ReceiverGroup[] groups = new ReceiverGroup[nrOfGroups];
        for(int i=0; i<nrOfGroups; i++) {
            groups[i] = new ReceiverGroup();
            groups[i].active = i%2==0;
            groups[i].group  = java.net.InetAddress.getByName("225.1.1." + i);
            groups[i].port   = 12345 + i;
            for(int j=0; j<rcvsPerGroup; j++) {
                Receiver rcv = new Receiver();
                rcv.alive = j%2==0;
                rcv.avgPPS = 123 + j*i;
                rcv.lostPackets = 456 + j*i;
                rcv.payload  = "Hello" + i + "" + j;
                rcv.senderConfiguredPPS = 135 + j*i;
                rcv.senderId = 421 + j*i;
                groups[i].add(rcv);
            }
            this.receiverGroupAdded(new ReceiverAddedOrRemovedEvent(groups[i]));
        }

        
        print();
    }*/

    private void print() {
        System.out.println(" nr | type | vis | visnum | state | group/senderid ");
        System.out.println("---------------------------------------------------");
        for(int i=0; i<rows.size(); i++) {
            ReceiverTableRow row = rows.get(i);
            if(row instanceof ReceiverGroupRow) {
                ReceiverGroupRow groupRow = (ReceiverGroupRow)row;
                System.out.println(   " " + i + " | "
                                    + "g" + " | "
                                    + groupRow.isVisible() + "| "
                                    + getVisibleIndex(groupRow) + "| "
                                    + ((groupRow.isExpanded()) ? "v" : ">") + " | "
                                    + groupRow.getReceiverGroup().getGroup().getHostAddress());
                System.out.flush();
            } else if(row instanceof ReceiverRow) {
                ReceiverRow rcvRow = (ReceiverRow)row;
                System.out.print(   " " );
                System.out.flush();
                System.out.print(i);
                System.out.flush();
                System.out.print(" | ");
                System.out.flush();
                System.out.print("r");
                System.out.flush();
                System.out.print(" | ");
                System.out.flush();
                System.out.print(rcvRow.isVisible());
                System.out.flush();
                System.out.print("| ");
                System.out.flush();
                System.out.print(getVisibleIndex(rcvRow));
                System.out.flush();
                System.out.print("| ");
                System.out.flush();
                System.out.print(" ");
                System.out.flush();
                System.out.print(" | ");
                System.out.flush();
                System.out.println(rcvRow.getReceiver().getSenderId());
                System.out.flush();
            }
        }
        System.out.println("---------------------------------------------------");
        System.out.flush();
    }

}
