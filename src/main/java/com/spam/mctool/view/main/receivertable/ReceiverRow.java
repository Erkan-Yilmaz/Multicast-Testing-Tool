package com.spam.mctool.view.main.receivertable;

import com.spam.mctool.model.Receiver;

/**
 * Model class representing a receiver's row.
 * @author Tobias Stöckel
 */
class ReceiverRow extends ReceiverTableRow {

    /**
     * The parent group row of this row
     */
    private final ReceiverGroupRow parent;

    /**
     * The receiver represented by this row
     */
    private final Receiver receiver;



    /**
     * Create a new receiver row for the specified receiver with the specified
     * parent.
     */
    ReceiverRow(ReceiverGroupRow parent, Receiver rcv) {
        this.parent = parent;
        this.receiver = rcv;
    }



    /**
     * Get the receiver represented by this row.
     * @return
     */
    public Receiver getReceiver() {
        return receiver;
    }



    /**
     * Get the parent of this row
     * @return
     */
    public ReceiverGroupRow getParent() {
        return parent;
    }

    


}
