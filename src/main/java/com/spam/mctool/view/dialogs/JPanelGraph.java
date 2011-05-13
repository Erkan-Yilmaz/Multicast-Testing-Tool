package com.spam.mctool.view.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * UI Component for drawing a graph
 *
 * @author Tobias Schoknecht (tobias.schoknecht@gmail.com)
 */
public class JPanelGraph extends javax.swing.JPanel {

    private List<Integer> list = new ArrayList<Integer>();
    private int maxPacketRate = 1;
    public static final int units = 11;

    /** 
     * Creates new form JPanelGraph and initialises the dataList
     */
    public JPanelGraph() {
        initArrayList();
    }

   /**
    * Initializes the maximum packet rate
    *
    * @param maxPacketRate Configured packet rate
    */
    public void setMaxPacketRate(int maxPacketRate){
        this.maxPacketRate = maxPacketRate;
    }

   /**
    * Initializes the list for drawing the graph with 0-values
    */
    private void initArrayList(){
        for(int i = 0; i < units; i++){
            list.add(0);
        }
    }

   /**
    * Inserts a new value to list and removes the first value
    *
    * @param val Value of the current measured packet rate
    */
    public void newVal(int val){
        list.add(val);
        list.remove(0);
        repaint();
    }

    @Override
   /**
    * Draws a black field with 4 horizontal and 9 vertical gray lines.
    * The graph is painted in green between the vertical lines.
    * The maximum drawn value on the vertical line is at 80% of the panel size
    */
    public void paintComponent(Graphics g){

        //get width and height set for this component
        int width = getWidth();
        int height = getHeight();

        //draw background in black
        //due to some weird bug it is down by black lines over all of the area of the component
        g.setColor(Color.BLACK);
        for(int i = 0; i <= height; i++){
            g.drawLine(0, i, width, i);
        }

        //draw help lines
        g.setColor(Color.GRAY);
        for(int i = 1; i < units-1; i++){
            g.drawLine(i*width/10, 0, i*width/10, height);
        }
        for(int i = 1; i < 5; i++){
            g.drawLine(0, i*height/5, width, i*height/5);
        }

        //draw the graph
        g.setColor(Color.GREEN);
        for(int i = 0; i < units-1; i++){

            g.drawLine(i*width/(units-1), Math.round((float)height-(float)list.get(i)/(float)maxPacketRate*((float)height*4/5)), (i+1)*width/(units-1), Math.round((float)height-(float)list.get(i+1)/(float)maxPacketRate*((float)height*4/5)));

        }

    }
    // Variables declaration - do not modify
    // End of variables declaration

}
