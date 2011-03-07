/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main;

/**
 *
 * @author Tobias Stöckel (Tobias.Stoeckel@de.ibm.com)
 */
public class ReceiverTableModel extends MulticastTableModel {

    public ReceiverTableModel() {
        addColumn("Lost Packets");
        addColumn("Interrupts");
        addColumn("max. Interrupt");
    }

}
