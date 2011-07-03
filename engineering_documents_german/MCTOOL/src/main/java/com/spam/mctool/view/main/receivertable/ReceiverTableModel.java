
package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;




/**
 * This class manages the receiver groups and receivers displayed in a
 * JReceiverTable. Provides support for adding and removing receivergroups and
 * according receivers. Prepares the containment hierarchy between receiver groups
 * and receivers. The ReceiverTableModel in fact also acts as controller for the
 * displaying table as it also manages actions such as expanding and collapsing of rows.
 * 
 * @author Tobias Stöckel
 */
public class ReceiverTableModel extends AbstractTableModel implements MouseListener {

    /**
     * List of rows contained in the model
     */
    private List<ReceiverTableRow> rows    = new ArrayList<ReceiverTableRow>();



    /**
     * Returns the number of <em>visible</em> (non-collapsed) rows.
     */
    public int getRowCount() {
        int count = 0;
        for(ReceiverTableRow row : rows) {
            if(row.isVisible()) count++;
        }
        return count;
    }



    /**
     * Returns the number of columns of the model
     */
    public int getColumnCount() {
        return ReceiverTableColumn.values().length;
    }



    /**
     * Maps the model's content to table coordinates. All data may be requested
     * by row and column index.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {

        // get the model object corresponding to the specified row index
        ReceiverTableRow row = getVisibleRow(rowIndex);
        
        if(row instanceof ReceiverGroupRow) {
            // if it is a group row, simply return it
            return (ReceiverGroupRow)row;

        } else if(row instanceof ReceiverRow) {
            // if it is a receiver row return the according column value

            ReceiverRow rcvRow      = (ReceiverRow)row;
            Receiver receiver       = rcvRow.getReceiver();

            // get the enum object corresponding to the selected column
            ReceiverTableColumn col = ReceiverTableColumn.values()[columnIndex];

            // depending on the desired column return the according receiver attribte
            InetAddress senderAddress;
            try {
                switch(col) {
                    case STATUS:    return receiver.isAlive();
                    case SENDER_ID: return receiver.getSenderId();
                    case SENDER_IP:
                        // Sorry for this hack: for some reason the sender address
                        // is not available immediately when requested (even though
                        // this access is explicitly synchronized) so we'll just
                        // have to try as long as it takes.
                        while((senderAddress = receiver.getSenderAddress()) == null) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ReceiverTableModel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        return senderAddress.getHostAddress();

                    case SENDER_CONF_PPS: return receiver.getSenderConfiguredPPS();
                    case AVG_PPS:         return receiver.getAvgPPS();
                    case LOST_PACKETS:    return receiver.getLostPackets() + " ‰";
                    case PAYLOAD:         return receiver.getPayloadAsString();
                    default:
                        throw new RuntimeException("Illegal columnIndex: " + columnIndex);
                }
            } catch (NullPointerException e) {
                // place for breakpoint for debugging the hack above
                throw e;
            }
        } else {
            throw new RuntimeException("Illegal Rowtype: " + row.getClass().getName());
        }
    }



    /**
     * Get the model object of a row based on its index in the view. The returned
     * row may either be a <code>ReceiverRow</code> or <code>ReceiverGroupRow</code>.
     */
    private ReceiverTableRow getVisibleRow(int rowIndex) {

        // iterate over all rows in the model until the rowIndex-th VISIBLE one
        // is found.
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



    /**
     * Notify the table model that a new receiver group was added to the application
     * model.
     */
    public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
        
        // wrap the receiver group in a new row object and append it to the list
        ReceiverGroup group = e.getSource();
        int newRowIndex     = getRowCount();
        rows.add(new ReceiverGroupRow(group));

        // notify the view that a new row was added
        this.fireTableRowsInserted(newRowIndex, newRowIndex);
    }



    /**
     * Notify the table model that a receiver group was removed from the application
     * model.
     */
    public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
        ReceiverGroup    group    = e.getSource();
        ReceiverGroupRow groupRow = getRow(group);

        // determine the number of rows that effectively are removed from the VIEW
        int firstRow = getIndex(group);
        int lastRow  = firstRow;

        // set lastRow to the last VISIBLE child's index in the view
        for(ReceiverRow row : groupRow.getReceiverRows()) {
            if(row.isVisible()) lastRow++;
            rows.remove(row);
        }
        rows.remove(groupRow);

        // notify the view about which rows were effectively deleted
        fireTableRowsDeleted(firstRow, lastRow);
    }

    
    
    /**
     * Notify the table model that the data of a contained receiver group and its
     * receivers has changed.
     */
    public void dataChanged(ReceiverDataChangedEvent e) {
        ReceiverGroup    receiverGroup    = e.getSource();
        ReceiverGroupRow receiverGroupRow = getRow(receiverGroup);
        int              groupRowIndex    = getVisibleIndex(receiverGroup);

        // update the row of the receiver group
        fireTableRowsUpdated(groupRowIndex, groupRowIndex);

        // update the rows of all receivers that are already displayed in the
        // view. Add remaining receivers to the table model
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



    /**
     * Return the "real" (not just visible) index of the row containing the
     * specified receiver group.
     * @return the list index of the row containing the group. -1 if no matching
     * row is found.
     */
    private int getIndex(ReceiverGroup group) {

        // iterate over the list of rows until the correct one is found
        for(int i=0; i<rows.size(); i++) {
            if(rows.get(i) instanceof ReceiverGroupRow) {
                ReceiverGroupRow row = (ReceiverGroupRow)rows.get(i);
                if(row.getReceiverGroup() == group) return i;
            }
        }
        return -1;
    }



    /**
     * Return the "real" (not just visible) index of the row containing the
     * specified receiver.
     * @return the list index of the row containing the receiver. -1 if no matching
     * row is found.
     */
    private int getIndex(Receiver receiver) {

         // iterate over the list of rows until the correct one is found
        for(int i=0; i<rows.size(); i++) {
            if(rows.get(i) instanceof ReceiverRow) {
                ReceiverRow row = (ReceiverRow)rows.get(i);
                if(row.getReceiver() == receiver) return i;
            }
        }
        return -1;
    }



    /**
     * Marks a group row as expanded and determines which additional receiver
     * rows have to be displayed now. Triggers the actual insertion of rows in
     * the view.
     *
     * @param visGroupRowIndex visible index of the group row that is to be expanded
     */
    public void expand(int visGroupRowIndex) {
        ReceiverGroupRow groupRow = (ReceiverGroupRow)getVisibleRow(visGroupRowIndex);
        if(!groupRow.isExpanded()) {

            // mark all contained receiver rows and visible and count them
            int newRowIndex = visGroupRowIndex;
            for(ReceiverRow receiverRow : groupRow.getReceiverRows()) {
                newRowIndex++;
                receiverRow.setVisible(true);
            }

            // mark the group row as expanded
            groupRow.setExpanded(true);

            // notify the table that the group row was changed (because the little
            // triangle has to flip) and that additional receiver rows were inserted
            this.fireTableRowsUpdated(visGroupRowIndex, visGroupRowIndex);
            this.fireTableRowsInserted(visGroupRowIndex+1, newRowIndex);

        } else {
            throw new RuntimeException("GroupRow already expanded: " + visGroupRowIndex);
        }
    }



    /**
     * Marks a group row as collapsed and removes the according receiver rows
     * from the view.
     *
     * @param visGroupRowIndex the visible index of the grou row whose receiver
     * rows are to be collapsed
     */
    void collapse(int visGroupRowIndex) {
        ReceiverGroupRow groupRow = (ReceiverGroupRow)getVisibleRow(visGroupRowIndex);
        if(groupRow.isExpanded()) {

            // mark the contained receiver rows as invisible and count them
            int rcvRowCount = 0;
            for(ReceiverRow rcvRow : groupRow.getReceiverRows()) {
                rcvRow.setVisible(false);
                rcvRowCount++;
            }

            // mark the group row as collapsed
            groupRow.setExpanded(false);

            // update the group row and notify the view that receiver rows are to
            // are to be deleted
            this.fireTableRowsUpdated(visGroupRowIndex, visGroupRowIndex);
            this.fireTableRowsDeleted(visGroupRowIndex+1, visGroupRowIndex+1+rcvRowCount);
            
        } else {
            throw new RuntimeException("GroupRow already collapsed: " + visGroupRowIndex);
        }
    }



    /**
     * Translates a double click into either an expansion or collapsing event.
     */
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {
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
    }



    /**
     * Does nothing. Just included for interface support.
     */
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }



    /**
     * Does nothing. Just included for interface support.
     */
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }



    /**
     * Does nothing. Just included for interface support.
     */
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }



    /**
     * Does nothing. Just included for interface support.
     */
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }



    /**
     * Get the row containing <code>receiverGroup</code>
     */
    private ReceiverGroupRow getRow(ReceiverGroup receiverGroup) {

        // scan all rows for the one containing the receiver group
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



    /**
     * Get the row containing <code>receiver</code>
     */
    private ReceiverRow getRow(Receiver receiver) {

        // scan all rows for the one containing the receiver
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



    /**
     * Checks if <code>receiver</code> is represented by one of the model's rows.
     * @return True, if the receiver is contained. False otherwise.
     */
    private boolean contains(Receiver receiver) {

        // scan all rows for the one containing the receiver
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



    /**
     * Add a receiver to the model. Notifies the view about the insertion if
     * the receiver's group is expanded.
     */
    private void addReceiver(ReceiverGroup group, Receiver receiver) {
        
        // get input position in row list
        ReceiverGroupRow groupRow      = getRow(group);
        int              groupRowIndex = rows.indexOf(groupRow);
        int              inputPos      = groupRowIndex + groupRow.getReceiverRowCount() + 1;

        // add a new row for the receiver
        ReceiverRow rcvRow = new ReceiverRow(groupRow, receiver);
        groupRow.add(rcvRow);
        rows.add(inputPos, rcvRow);

        if(groupRow.isExpanded()) {

            // determine the visible index of the new row and update the view accordingly
            rcvRow.setVisible(true);
            int visInputPos = getVisibleIndex(rcvRow);
            this.fireTableRowsInserted(visInputPos, visInputPos);
        }
    }



    /**
     * Get the visible index of a receiver row. This takes into account that not
     * all rows in the model have to be visible as they may be collapsed.
     */
    private int getVisibleIndex(ReceiverTableRow searchRow) {

        // scan all rows and count only visible ones
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



    /**
     * Get the visible index of a receiver group row. This takes into account that not
     * all rows in the model have to be visible as they may be collapsed.
     */
    private int getVisibleIndex(ReceiverGroup receiverGroup) {
        
        // scan all rows and count only visible ones
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



    /**
     * Returns the receiverGroup that is associated with the
     * given row index. If the rowIndex identifies a row representing a
     * receiverGroup, that receiverGroup is returned. If rowIndex identifies
     * a row representing a Receiver, nothing is returned
     * @return the ReceiverGroup contained by the given row. Null if <code>rowIndex</code>
     * identifies a receiver row.
     */
    ReceiverGroup getReceiverGroupAt(int rowIndex) {
        ReceiverTableRow row = getVisibleRow(rowIndex);
        if(row instanceof ReceiverGroupRow) {
            return ((ReceiverGroupRow)row).getReceiverGroup();
        } else if(row instanceof ReceiverRow) {
            return null;
        } else {
            throw new RuntimeException("Illegal Rowtype: " + row.getClass().getName());
        }
    }




    /**
     * Returns the receiver that is represented by the given row. If rowIndex
     * identifies a row containing a ReceiverGroup, null is returned.
     * @param rowIndex
     * @return the Receiver represented by row number rowIndex. Null, if
     *         row number rowIndex represents a ReceiverGroup.
     */
    Receiver getReceiverAt(int rowIndex) {
        ReceiverTableRow row = getVisibleRow(rowIndex);
        if(row instanceof ReceiverRow) {
            ReceiverRow rcvRow = (ReceiverRow)row;
            return rcvRow.getReceiver();
        }
        else if(row instanceof ReceiverGroupRow) {
            return null;
        } else {
            throw new RuntimeException("Illegal Rowtype: " + row.getClass().getName());
        }
    }

    
    
    /**
     * Returns the receiver group containing the receiver <code>r</code>
     */
    ReceiverGroup getParent(Receiver r) {
        return this.getRow(r).getParent().getReceiverGroup();
    }



    /**
     * Notify the table model that a receiver was explicitly removed from the
     * table.
     */
    void receiverRemoved(Receiver r) {
        ReceiverRow receiverRow = getRow(r);
        int rowIndex = this.getVisibleIndex(receiverRow);

        // remove the receiver row from the group row containing it
        receiverRow.getParent().remove(receiverRow);

        // remove the receiver row from the row list
        rows.remove(receiverRow);

        // update the view
        this.fireTableRowsDeleted(rowIndex, rowIndex);
    }

}
