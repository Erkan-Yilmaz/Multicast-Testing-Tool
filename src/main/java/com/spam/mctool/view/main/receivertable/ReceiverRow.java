/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.model.Receiver;

/**
 *
 * @author Tobias Stöckel
 */
public class ReceiverRow extends ReceiverTableRow {

    private final ReceiverGroupRow parent;
    private final Receiver receiver;

    ReceiverRow(ReceiverGroupRow parent, Receiver rcv) {
        this.parent = parent;
        this.receiver = rcv;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public ReceiverGroupRow getParent() {
        return parent;
    }

    


}
