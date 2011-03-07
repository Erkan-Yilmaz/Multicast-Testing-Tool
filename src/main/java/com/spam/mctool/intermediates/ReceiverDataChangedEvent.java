package com.spam.mctool.intermediates;

import com.spam.mctool.model.Receiver;

public class ReceiverDataChangedEvent {

    private Receiver source;

    public ReceiverDataChangedEvent(Receiver source) {
        this.source = source;
    }

    public Receiver getSource() {
        return source;
    }
}
