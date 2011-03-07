/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tobias Stöckel (Tobias.Stoeckel@de.ibm.com)
 */
public abstract class MulticastTableModel extends DefaultTableModel {
    protected Class[] types = new Class [] { };

    public MulticastTableModel() {
        System.out.println("MulticastTableModel Constructor called");
        addColumn("Group");
        addColumn("Port");
        addColumn("Shouldbe Packet Rate");
        addColumn("Measured Packet Rate");
        addColumn("Status");
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }
}
