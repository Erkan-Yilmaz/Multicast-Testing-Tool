package com.spam.mctool.intermediates;

import com.spam.mctool.model.Receiver;

public class ReceiverAddedOrRemovedEvent {

    private Receiver source;

    public ReceiverAddedOrRemovedEvent(Receiver r) {
        source = r;
    }

    public Receiver getSource() {
        return source;
    }
}
