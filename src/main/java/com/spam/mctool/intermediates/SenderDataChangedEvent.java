package com.spam.mctool.intermediates;

import com.spam.mctool.model.Sender;
import com.spam.mctool.model.SenderStatistics;

public class SenderDataChangedEvent {

    private Sender source;
    private SenderStatistics stats;

    public SenderDataChangedEvent(Sender source, SenderStatistics stats) {
        this.source = source;
        this.stats = stats;
        
        System.out.println("com.spam.mctool.intermediates.SenderDataChangedEvent says: "+source.getSenderId()+"|"+stats.getSentPackets()+"|"+stats.getMinPPS()+"|"+stats.getAvgPPS()+"|"+stats.getMaxPPS());
    }

    public Sender getSource() {
        return source;
    }
    
    public SenderStatistics getStatistics() {
    	return stats;
    }
    
}
