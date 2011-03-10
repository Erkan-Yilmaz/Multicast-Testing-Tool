package com.spam.mctool.intermediates;

import java.util.Collection;

import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverStatistics;

public class ReceiverDataChangedEvent {
	
    private Receiver source;
    private Collection<ReceiverStatistics> stats;

    public ReceiverDataChangedEvent(Receiver source) {
        this.source = source;
    }

    public Receiver getSource() {
        return source;
    }
    
    public Collection<ReceiverStatistics> getStatistics() {
    	return stats;
    }
}
