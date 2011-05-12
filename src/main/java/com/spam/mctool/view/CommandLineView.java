package com.spam.mctool.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
 *
 * @author ramin
 */
public class CommandLineView implements MctoolView, ProfileChangeListener, ReceiverAddedOrRemovedListener, SenderAddedOrRemovedListener, ErrorEventListener, OverallReceiverStatisticsUpdatedListener, OverallSenderStatisticsUpdatedListener {
	private PrintStream out = System.out;
	private BufferedWriter log;
	private Controller c;
	private Date lastUpdate = null;
	private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle");
	
	private Logger logger;
	
	public CommandLineView() {
		logger = Logger.getRootLogger();
		SimpleLayout layout = new SimpleLayout();
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
	}
	
    public void init(Controller c) {
        //throw new UnsupportedOperationException("Not supported yet.");
    	this.c = c;
        c.addProfileChangeListener(this);
        c.addReceiverAddedOrRemovedListener(this);
        c.addSenderAddedOrRemovedListener(this);
        c.addErrorEventListener(this, ErrorEventManager.WARNING);
        c.addOverallReceiverStatisticsUpdatedListener(this);
        c.addOverallSenderStatisticsUpdatedListener(this);
        
        try {
			log = new BufferedWriter(new FileWriter("log.txt", true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        logger.info(bundle.getString("CommandLine.LoggerInitialized.text")+" "+new Date());
    }

	public void profileChanged(ProfileChangeEvent e) {
		out.println(bundle.getString("CommandLine.ProfileChanged.text"));
		
		logger.info(bundle.getString("CommandLine.ProfileChanged.text"));
			
	}

	public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.ReceiverGroupAdded.text") + e.getSource().getGroup().toString());
		logger.info(bundle.getString("CommandLine.ReceiverGroupAdded.text") + e.getSource().getGroup().toString());
	
	}

	public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.ReceiverGroupRemoved.text") + e.getSource().getGroup().toString());
		
		logger.info(bundle.getString("CommandLine.ReceiverGroupRemoved.text") + e.getSource().getGroup().toString());
			
	}

	public void senderAdded(SenderAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.SenderAdded.text") + "ID:" + e.getSource().getSenderId());
		
		logger.info(bundle.getString("CommandLine.SenderAdded.text") + "ID:" + e.getSource().getSenderId());
		
	}

	public void senderRemoved(SenderAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.SenderRemoved.text")  + "ID:" + e.getSource().getSenderId());
		
		logger.info(bundle.getString("CommandLine.SenderRemoved.text")  + "ID:" + e.getSource().getSenderId());
		
	}
	
	@Override
	public void kill()
	{
		c.removeProfileChangeListener(this);
        c.removeReceiverAddedOrRemovedListener(this);
        c.removeSenderAddedOrRemovedListener(this);
        c.removeOverallReceiverStatisticsUpdatedListener(this);
        c.removeOverallSenderStatisticsUpdatedListener(this);
		
        logger.info(bundle.getString("CommandLine.Kill.text") + new Date());
        
        try {
			log.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void newErrorEvent(ErrorEvent e) {
		Date date = new Date();
		
		if(e.getErrorLevel() == ErrorEventManager.WARNING)
			logger.info(date.toString() + " - " + bundle.getString("CommandLine.Warning.text") + e.getCompleteMessage());
		else
			
		out.println(date.toString() + " - " + bundle.getString("CommandLine.Error.text") + e.getCompleteMessage());
	}

        /**
         * {@inheritDoc }
         *
         * <p>This implementation will print all internal exceptions' stack traces.
         *    This is because this method will be called by the model's threading
         *    framework which conveniently "swallows" all exceptions.</p>
         *
         * @param e
         */
	@Override
	public void overallSenderStatisticsUpdated(
			OverallSenderStatisticsUpdatedEvent e) {
            try {
		if(lastUpdate == null){
			lastUpdate = new Date();
		}
		
		if(lastUpdate.getTime() > 5.*60.*1000){
                        Date date = new Date();
			out.println(bundle.getString("CommandLine.SenderStatistics.text") + date.toString() + ":\n" );
			out.println(bundle.getString("CommandLine.SentPackets.text") + e.getSource().getOverallSentPackets() + "\n");
			out.println(bundle.getString("CommandLine.SentPacketsPerSec.text") + e.getSource().getOverallSentPPS() + "\n");
			
			logger.info(bundle.getString("CommandLine.SenderStatistics.text") + date.toString() + ":\n");
			logger.info(bundle.getString("CommandLine.SentPackets.text") + e.getSource().getOverallSentPackets() + "\n");
			logger.info(bundle.getString("CommandLine.SentPacketsPerSec.text") + e.getSource().getOverallSentPPS() + "\n");
			
		}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
		
	}

        /**
         * {@inheritDoc }
         *
         * <p>This implementation will print all internal exceptions' stack traces.
         *    This is because this method will be called by the model's threading
         *    framework which conveniently "swallows" all exceptions.</p>
         *
         * @param e
         */
	@Override
	public void overallReceiverStatisticsUpdated(
			OverallReceiverStatisticsUpdatedEvent e) {
            try {
		if(lastUpdate == null){
			lastUpdate = new Date();
		}
		
		if(lastUpdate.getTime() > 5.*60.*1000){
                        Date date = new Date();
			out.println(bundle.getString("CommandLine.ReceiverStatistics.text") + date.toString() + ":\n" );
			out.println(bundle.getString("CommandLine.ReceivedPackages.text") + e.getSource().getOverallReceivedPackets() + "\n");
			out.println(bundle.getString("CommandLine.FaultyPackets.text") + e.getSource().getOverallFaultyPackets() + "\n");
			out.println(bundle.getString("CommandLine.LostPackages.text") + e.getSource().getOverallLostPackets() + "\n");
			out.println(bundle.getString("CommandLine.ReceivedPaketsPerSec.text") + e.getSource().getOverallReceivedPPS() + "\n");
			
			logger.info(bundle.getString("CommandLine.ReceiverStatistics.text") + date.toString() + ":\n" );
			logger.info(bundle.getString("CommandLine.ReceivedPackages.text") + e.getSource().getOverallReceivedPackets() + "\n");
			logger.info(bundle.getString("CommandLine.FaultyPackets.text") + e.getSource().getOverallFaultyPackets() + "\n");
			logger.info(bundle.getString("CommandLine.LostPackages.text") + e.getSource().getOverallLostPackets() + "\n");
			logger.info(bundle.getString("CommandLine.ReceivedPaketsPerSec.text") + e.getSource().getOverallReceivedPPS() + "\n");
		}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
	}

}
