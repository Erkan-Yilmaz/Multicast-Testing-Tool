package com.spam.mctool.intermediates;

import com.spam.mctool.model.ReceiverGroup;

public class ReceiverAddedOrRemovedEvent {

    private ReceiverGroup source;

    public ReceiverAddedOrRemovedEvent(ReceiverGroup r) {
        source = r;
    }

    public ReceiverGroup getSource() {
        return source;
    }
}
