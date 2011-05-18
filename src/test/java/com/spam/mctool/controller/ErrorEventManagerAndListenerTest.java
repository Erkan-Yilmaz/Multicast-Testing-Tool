package com.spam.mctool.controller;

import static org.junit.Assert.*;

import org.junit.Test;

public class ErrorEventManagerAndListenerTest implements ErrorEventListener{

    private int x = 0;
    private ErrorEvent event = null;

    @Test
    public void testAddErrorEventListener() {
        Controller controller = Controller.getController();
        //report an error, nothing should happen
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"",""));
        //x is still the same
        assertEquals(x,0);
        //register as observer for all ErrorEvents
        controller.addErrorEventListener(this, ErrorEventManager.DEBUG);
        //Debug error should be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"",""));
        //x has changed
        assertEquals(x,1);
        //reset x
        x = 0;
        //Error should be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"",""));
        //x has changed
        assertEquals(x,1);
        //reset x
        x = 0;
        //Severe error should be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.SEVERE,"",""));
        //x has changed
        assertEquals(x,1);
        //reset x
        x = 0;
        //Critical error should be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.CRITICAL,"",""));
        //x has changed
        assertEquals(x,1);
        //reset x
        x = 0;
        //Fatal error should be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL,"",""));
        //x has changed
        assertEquals(x,1);
        //reset x
        x = 0;
        //register as oberver for ErrorEvents only wanting FATAL Errors
        controller.addErrorEventListener(this, ErrorEventManager.FATAL);
        //reset x
        x = 0;
        //All error events should not be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"",""));
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"",""));
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.CRITICAL,"",""));
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.SEVERE,"",""));
        assertEquals(x, 0);
        //Report an fatal error
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL,"",""));
        //x should have changed
        assertEquals(x, 1);
        //register as oberver for ErrorEvents wanting CRITICAL and higer
        controller.addErrorEventListener(this, ErrorEventManager.CRITICAL);
        //reset x
        x = 0;
        //These error events should not be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"",""));
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"",""));
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.SEVERE,"",""));
        assertEquals(x, 0);
        //Report an fatal error
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.FATAL,"",""));
        //x should have changed
        assertEquals(x, 1);
        //reset x
        x = 0;
        //Report an fatal error
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.CRITICAL,"",""));
        //x should have changed
        assertEquals(x, 1);
        //try to add null will result in an exception
        try{
            controller.removeErrorEventListener(null);
            fail("Exception not thrown");
        }
        catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testRemoveErrorEventListener() {
        Controller controller = Controller.getController();
        //report an error, nothing should happen
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.ERROR,"",""));
        //x is still the same
        assertEquals(x,0);
        //register as observer for all ErrorEvents
        controller.addErrorEventListener(this, ErrorEventManager.DEBUG);
        //Debug error should be reported
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"",""));
        //x has changed
        assertEquals(x,1);
        //reset x
        x = 0;
        //remove from the observer list
        controller.removeErrorEventListener(this);
        //report the new array again
        controller.reportErrorEvent(new ErrorEvent(ErrorEventManager.DEBUG,"",""));
        //x has not changed
        assertEquals(x,0);
        //try to remove null will result in an exception
        try{
            controller.removeErrorEventListener(null);
            fail("Exception not thrown");
        }
        catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testReportErrorEvent() {
        Controller controller = Controller.getController();
        //register as observer
        controller.addErrorEventListener(this, ErrorEventManager.DEBUG);
        //Create a new errorevent
        ErrorEvent testEvent = new ErrorEvent(ErrorEventManager.DEBUG,"testidentifier","testmessage");
        controller.reportErrorEvent(testEvent);
        //make sure the event has not been modified
        assertEquals(event,testEvent);
        //Try to report null will result in an exception
        try{
            controller.reportErrorEvent(null);
            fail("Exception not thrown");
        }
        catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
    }


    @Override
    public void newErrorEvent(ErrorEvent e) {
        //set x to 1
        this.x = 1;
        this.event = e;
    }

}
