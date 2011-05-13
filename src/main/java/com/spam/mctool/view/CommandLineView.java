package com.spam.mctool.view;

import java.io.IOException;
import java.util.Date;
import org.apache.log4j.*;

import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ErrorEvent;
import com.spam.mctool.controller.ErrorEventListener;
import com.spam.mctool.controller.ErrorEventManager;
import com.spam.mctool.controller.ProfileChangeListener;
import com.spam.mctool.intermediates.OverallReceiverStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.OverallSenderStatisticsUpdatedEvent;
import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.model.OverallReceiverStatisticsUpdatedListener;
import com.spam.mctool.model.OverallSenderStatisticsUpdatedListener;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.SenderAddedOrRemovedListener;

/**
 *This class creates the output for the command line and logs it.
 * @author ramin
 */
public class CommandLineView implements MctoolView, ProfileChangeListener, ReceiverAddedOrRemovedListener, SenderAddedOrRemovedListener, ErrorEventListener, OverallReceiverStatisticsUpdatedListener, OverallSenderStatisticsUpdatedListener {
	private int offset = 10*1000;
	private Controller c;
	private Date lastUpdateSnd = null;
	private Date lastUpdateRcv = null;
	private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle");
	
	private Logger logger;
	
	public CommandLineView() {
		
		//get logger object
		logger = Logger.getRootLogger();
		
		//logger prints just the message 
		PatternLayout layout = new PatternLayout("[%d{dd.MM.yyyy HH:mm:ss}] - %m%n");
		
		
		//logger writes to file
		try {
			FileAppender fa = new FileAppender(layout, "log.txt");
			logger.addAppender(fa);
		} catch (IOException e) {
			
			ErrorEvent err = new ErrorEvent();
			err.setAdditionalErrorMessage("Couldn't open log file");
			
			c.reportErrorEvent(err);
		}
		
		//logger prints to console
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
		
	}
	
	/**
	 * This method initializes the CommandLineView which means that it registers
	 * as Listener for relevant Events
	 * 
	 * @param c Controller that informs about the Events 
	 */
    public void init(Controller c) {
       
    	this.c = c;
    	
    	//register as listener
        c.addProfileChangeListener(this);
        c.addReceiverAddedOrRemovedListener(this);
        c.addSenderAddedOrRemovedListener(this);
        c.addErrorEventListener(this, ErrorEventManager.WARNING);
        c.addOverallReceiverStatisticsUpdatedListener(this);
        c.addOverallSenderStatisticsUpdatedListener(this);
        
        //inform that tool was started
        logger.info(bundle.getString("CommandLine.LoggerInitialized.text"));
    }

    /**
     * called when the user changes the profile
     */
	public void profileChanged(ProfileChangeEvent e) {
		
		logger.info(bundle.getString("CommandLine.ProfileChanged.text"));
			
	}

	/**
	 * called when a receiver has been added
	 */
	public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
		logger.info(bundle.getString("CommandLine.ReceiverGroupAdded.text") + e.getSource().getGroup().toString());
	
	}

	/**
	 * called when a receiver has been removed
	 */
	public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
		
		logger.info(bundle.getString("CommandLine.ReceiverGroupRemoved.text") + e.getSource().getGroup().toString());
			
	}

	/**
	 * called when a sender has been added
	 */
	public void senderAdded(SenderAddedOrRemovedEvent e) {
		
		logger.info(bundle.getString("CommandLine.SenderAdded.text") + " ID:" + e.getSource().getSenderId());
		
	}

	/**
	 * called when a sender has been removed
	 */
	public void senderRemoved(SenderAddedOrRemovedEvent e) {
		
		logger.info(bundle.getString("CommandLine.SenderRemoved.text")  + " ID:" + e.getSource().getSenderId());
		
	}
	
	/**
	 * This method is used to unregister as listener from all registered events
	 */
	@Override
	public void kill()
	{
		//unregister from events
		c.removeProfileChangeListener(this);
        c.removeReceiverAddedOrRemovedListener(this);
        c.removeSenderAddedOrRemovedListener(this);
        c.removeOverallReceiverStatisticsUpdatedListener(this);
        c.removeOverallSenderStatisticsUpdatedListener(this);
		
        //inform that tool stopped
        logger.info(bundle.getString("CommandLine.Kill.text"));
        
	}

	/**
	 * called when an error occurred
	 */
	@Override
	public void newErrorEvent(ErrorEvent e) {
		Date date = new Date();
		
		if(e.getErrorLevel() == ErrorEventManager.WARNING)
			logger.info(bundle.getString("CommandLine.Warning.text") + e.getCompleteMessage());
		else
			
		logger.info(bundle.getString("CommandLine.Error.text") + e.getCompleteMessage());
	}

    /**
     * called when new sender statistics are available
     */
	@Override
	public void overallSenderStatisticsUpdated(
			OverallSenderStatisticsUpdatedEvent e) {
            
		if(lastUpdateSnd == null){
			lastUpdateSnd = new Date();
		}
		
		if(new Date().getTime() - lastUpdateSnd.getTime() > offset){
            
			if(e.getSource().getOverallSentPackets() != 0)
			{
				Date date = new Date();
			
				logger.info(
						bundle.getString("CommandLine.SenderStatistics.text") + date.toString() + "\r\n\r\n" + 
						bundle.getString("CommandLine.SentPackets.text") + e.getSource().getOverallSentPackets() + "\r\n" +
						bundle.getString("CommandLine.SentPacketsPerSec.text") + e.getSource().getOverallSentPPS() + "\r\n");
				lastUpdateSnd = new Date();
			}
		}
            
		
	}

    /**
     * called when new receiver statistics are available
     */
	@Override
	public void overallReceiverStatisticsUpdated(
			OverallReceiverStatisticsUpdatedEvent e) {
            
		if(lastUpdateRcv == null){
			lastUpdateRcv = new Date();
		}
		
		if(new Date().getTime() - lastUpdateRcv.getTime() > offset){
            
			if(e.getSource().getOverallReceivedPackets() != 0)
			{
				Date date = new Date();

				logger.info(
						bundle.getString("CommandLine.ReceiverStatistics.text") + date.toString() + ":\r\n\r\n" +
						bundle.getString("CommandLine.ReceivedPackages.text") + e.getSource().getOverallReceivedPackets() + "\r\n" +
						bundle.getString("CommandLine.FaultyPackets.text") + e.getSource().getOverallFaultyPackets() + "\r\n" +
						bundle.getString("CommandLine.LostPackages.text") + e.getSource().getOverallLostPackets() + "\r\n" +
						bundle.getString("CommandLine.ReceivedPaketsPerSec.text") + e.getSource().getOverallReceivedPPS());
				lastUpdateRcv = new Date();
			}
		}

	}
}
