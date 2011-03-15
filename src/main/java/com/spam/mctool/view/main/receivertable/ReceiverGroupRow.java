/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

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

    ReceiverGroup getReceiverGroup() {
        return group;
    }

    void addReceiverRow(ReceiverRow receiverRow) {
        receiverRows.add(receiverRow);
    }

    boolean isExpanded() {
        return expanded;
    }

    Iterable<ReceiverRow> getReceiverRows() {
        return receiverRows;
    }

    void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}
