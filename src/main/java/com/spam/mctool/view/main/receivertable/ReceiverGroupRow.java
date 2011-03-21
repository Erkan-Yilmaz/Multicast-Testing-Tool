/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tobias Stöckel
 */
class ReceiverGroupRow extends ReceiverTableRow {
    private final ReceiverGroup group;
    private List<ReceiverRow> receiverRows = new ArrayList<ReceiverRow>();
    private boolean expanded = false;
    
    ReceiverGroupRow(ReceiverGroup group) {
        this.group = group;
    }

    void addReceiverRow(ReceiverRow receiverRow) {
        receiverRows.add(receiverRow);
    }

    boolean isExpanded() {
        return expanded;
    }

    void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    boolean contains(Receiver receiver) {
        for(ReceiverRow row : receiverRows) {
            if(row.getReceiver() == receiver) {
                return true;
            }
        }
        return false;
    }

    ReceiverGroup getReceiverGroup() {
        return group;
    }

    int getReceiverRowCount() {
        return receiverRows.size();
    }

    Iterable<ReceiverRow> getReceiverRows() {
        return receiverRows;
    }

    

}