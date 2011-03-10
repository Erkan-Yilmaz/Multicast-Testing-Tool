package com.spam.mctool.intermediates;

import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderStatistics;

public class SenderDataChangedEvent {

    private Sender source;
    private SenderStatistics stats;

    public SenderDataChangedEvent(Sender source) {
        this.source = source;
    }

    public Sender getSource() {
        return source;
    }
    
    public SenderStatistics getStatistics() {
    	return stats;
    }
    
}
