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
class ReceiverRow extends ReceiverTableRow {

    private final Receiver receiver;

    ReceiverRow(Receiver rcv) {
        this.receiver = rcv;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    



}
