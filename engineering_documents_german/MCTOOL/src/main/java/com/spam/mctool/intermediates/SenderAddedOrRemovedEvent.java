package com.spam.mctool.intermediates;

import com.spam.mctool.model.Sender;

public class SenderAddedOrRemovedEvent {

    private Sender source;

    public SenderAddedOrRemovedEvent(Sender source) {
        this.source = source;
    }

    public Sender getSource() {
        return source;
    }
}
