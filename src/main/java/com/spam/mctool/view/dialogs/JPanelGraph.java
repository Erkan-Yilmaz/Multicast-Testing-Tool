package com.spam.mctool.view.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Tobias Schoknecht (Tobias.Schoknecht@de.ibm.com)
 */
public class JPanelGraph extends javax.swing.JPanel {

    ArrayList<Integer> list = new ArrayList<Integer>();
    int maxPacketRate = 1;

    /** Creates new form JPanelGraph */
    public JPanelGraph() {
        initArrayList();
    }

    public void setMaxPacketRate(int maxPacketRate){
        this.maxPacketRate = maxPacketRate;
    }

    private void initArrayList(){
        for(int i = 0; i < 11; i++){
            list.add(0);
        }
    }

    public void newVal(int val){
        list.add(val);
        list.remove(0);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.BLACK);
        for(int i = 0; i <= height; i++){
            g.drawLine(0, i, width, i);
        }

        g.setColor(Color.GRAY);
        for(int i = 1; i < 10; i++){
            g.drawLine(i*width/10, 0, i*width/10, height);
        }
        for(int i = 1; i < 4; i++){
            g.drawLine(0, i*height/4, width, i*height/4);
        }

        g.setColor(Color.GREEN);
        for(int i = 0; i < 10; i++){

            g.drawLine(i*width/10, list.get(i)/maxPacketRate*height, (i+1)*width/10, list.get(i+1)/maxPacketRate*height);

        }

    }
    // Variables declaration - do not modify
    // End of variables declaration

}
