package com.spam.mctool.intermediates;

import com.spam.mctool.model.Sender;

public class SenderDataChangedEvent {

    private Sender source;

    public SenderDataChangedEvent(Sender source) {
        this.source = source;
    }

    public Sender getSource() {
        return source;
    }
    
}
