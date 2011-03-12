package com.spam.mctool.intermediates;

import java.util.ArrayList;
import java.util.List;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;

public class ReceiverDataChangedEvent {
	
    private ReceiverGroup source;
    private List<Receiver> receivers;

    public ReceiverDataChangedEvent(ReceiverGroup source) {
        this.source = source;
        this.receivers = new ArrayList<Receiver>();
    }

    public ReceiverGroup getSource() {
        return source;
    }
    
    public List<Receiver> getReceiverList() {
    	return this.receivers;
    }
    
}
