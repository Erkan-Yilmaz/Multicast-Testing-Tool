package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import java.util.ArrayList;
import java.util.List;



/**
 * Model class representing a receiver group's row and the rows of the receivers
 * it contains.
 * @author Tobias Stöckel
 */
class ReceiverGroupRow extends ReceiverTableRow {

    /**
     * the group represented by this row
     */
    private final ReceiverGroup group;

    /**
     * the rows of this group's receivers
     */
    private List<ReceiverRow> receiverRows = new ArrayList<ReceiverRow>();

    /**
     * whether the row is currently expanded in the view
     */
    private boolean expanded = true;
    


    /**
     * Creates a new receiver group row for the specified receiver group.
     */
    ReceiverGroupRow(ReceiverGroup group) {
        this.group = group;
        // group rows are always visible
        this.setVisible(true);
    }



    /**
     * Add a new receiver's row to this row
     */
    void add(ReceiverRow receiverRow) {
        receiverRows.add(receiverRow);
    }


    /**
     * Returns whether the receiver rows contained in this row are currently
     * expanded in the view.
     */
    boolean isExpanded() {
        return expanded;
    }



    /**
     * Mark this row as expanded
     * @param expanded
     */
    void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    /**
     * Determines whether one of this row's children represents the specified
     * receiver.
     */
    boolean contains(Receiver receiver) {
        for(ReceiverRow row : receiverRows) {
            if(row.getReceiver() == receiver) {
                return true;
            }
        }
        return false;
    }



    /**
     * Get the group represented by this row.
     */
    ReceiverGroup getReceiverGroup() {
        return group;
    }



    /**
     * Get the number of receiver rows contained by this row.
     */
    int getReceiverRowCount() {
        return receiverRows.size();
    }



    /**
     * Get a list of all receiver rows contained by this row.
     * @return
     */
    List<ReceiverRow> getReceiverRows() {
        return receiverRows;
    }



    /**
     * Remove a receiver row from this row's children.
     */
    void remove(ReceiverRow rrow) {
        receiverRows.remove(rrow);
    }

    

}
