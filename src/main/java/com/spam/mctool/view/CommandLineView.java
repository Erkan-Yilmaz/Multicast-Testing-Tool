package com.spam.mctool.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

import com.spam.mctool.controller.Controller;
import com.spam.mctool.controller.ProfileChangeListener;
import com.spam.mctool.intermediates.ProfileChangeEvent;
import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.SenderAddedOrRemovedEvent;
import com.spam.mctool.model.ReceiverAddedOrRemovedListener;
import com.spam.mctool.model.SenderAddedOrRemovedListener;

/**
 *
 * @author ramin
 */
public class CommandLineView implements MctoolView, ProfileChangeListener, ReceiverAddedOrRemovedListener, SenderAddedOrRemovedListener {
	private PrintStream out = System.out;
	private BufferedWriter log;
	private Controller c;
	private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("internationalization/Bundle");
	
	
    public void init(Controller c) {
        //throw new UnsupportedOperationException("Not supported yet.");
    	this.c = c;
        c.addProfileChangeListener(this);
        c.addReceiverAddedOrRemovedListener(this);
        c.addSenderAddedOrRemovedListener(this);
        c.removeProfileChangeListener(this);
        c.removeReceiverAddedOrRemovedListener(this);
        c.removeSenderAddedOrRemovedListener(this);
        
        try {
        	log = new BufferedWriter(new FileWriter("log.txt", true));
        	
        }
        catch(IOException e){
        }
        
       
    }

	public void profileChanged(ProfileChangeEvent e) {
		out.println(bundle.getString("CommandLine.ProfileChanged.text"));
		
		try {
		    
		    log.write(bundle.getString(("CommandLine.ProfileChanged.text")));
		    log.close();
		} catch (IOException evt) {
		}
			
	}

	public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.ReceiverGroupAdded.text"));
		
		try {
		    
		    log.write(bundle.getString(("CommandLine.ReceiverGroupAdded.text")));
		    log.close();
		} catch (IOException evt) {
		}
	
	}

	public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.ReceiverGroupRemoved.text"));
		
		try {
		    
		    log.write(bundle.getString(("CommandLine.ReceiverGroupRemoved.text")));
		    log.close();
		} catch (IOException evt) {
		}
			
	}

	public void senderAdded(SenderAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.SenderAdded.text"));
		
		try {
		    
		    log.write(bundle.getString(("CommandLine.SenderAdded.text")));
		    log.close();
		} catch (IOException evt) {
		}
		
	}

	public void senderRemoved(SenderAddedOrRemovedEvent e) {
		out.println(bundle.getString("CommandLine.SenderRemoved.text"));
		
		try {
		    
		    log.write(bundle.getString(("CommandLine.SenderRemoved.text")));
		    log.close();
		} catch (IOException evt) {
		}
		
	}

}
