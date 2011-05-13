package com.spam.mctool.view.main.receivertable;


import com.spam.mctool.intermediates.ReceiverAddedOrRemovedEvent;
import com.spam.mctool.intermediates.ReceiverDataChangedEvent;
import com.spam.mctool.model.Receiver;
import com.spam.mctool.model.ReceiverGroup;
import com.spam.mctool.view.main.TwoColorRenderer;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;



/**
 * Specializes a JTable for representing ReceiverGroups and Receivers.
 * A JReceiverTable presents each ReceiverGroup as one row and a collapsible
 * set of subrows that represent the Receivers associated with this ReceiverGroup.
 * JReceiverTable is tightly coupled with the ReceiverTableModel and will throw
 * IllegalStateExceptions when trying to access ReceiverTable specific actions
 * while its model is no subtype of ReceiverTableModel.
 *
 * @author Tobias Stöckel
 */
public class JReceiverTable extends JTable {



    /**
     * The renderer that will be used for rendering a row representing a
     * ReceiverGroup.
     */
    private final GroupRowRenderer groupRowRenderer = new GroupRowRenderer();



    /**
     * Creates and initializes a new JReceiverTable with a ReceiverTableModel.
     * This parameterless constructor has mainly been added for better
     * integration with the netbeans gui designer.
     */
    public JReceiverTable() {
        this(new ReceiverTableModel());
    }



    /**
     * Creates and initializes a new JReceiverTable based on the passed
     * ReceiverTableModel.
     * @param model
     */
    public JReceiverTable(ReceiverTableModel model) {
        super(model);
        this.init();
    }

    
    
    /**
     * Initializes the JReceiverTable and its components.
     */
    private void init() {

        // Set column names and headers
        ReceiverTableColumn[] cols = ReceiverTableColumn.values();
        for(int i=0; i<cols.length; i++) {
            this.getColumnModel().getColumn(i).setHeaderValue(cols[i].getCaption());
        }

        // Add default cell renderers and register the table model as mouse
        // listener (for collapsing/expanding). The special cell renderer for
        // ReceiverGroups will be directly provided by the getCellRenderer-method.
        this.getColumnModel().getColumn(0).setCellRenderer(new ReceiverStateRenderer());
        this.addMouseListener((ReceiverTableModel)this.getModel());
        this.setDefaultRenderer(Object.class, new TwoColorRenderer());
    }

    
    
    /**
     * <p>Returns an appropriate renderer for the cell specified by this row and
     * column.</p>
     * <p>For table rows representing receivers, the default renders defined during
     * initialization will be returned. For table rows representing receiver groups,
     * a special renderer with folding capabilities will be returned.</p>
     * @param row
     * @param column
     * @return
     */
    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(getValueAt(row,column) instanceof ReceiverGroupRow) {
            return groupRowRenderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }



    /**
     * Notify the table that a new receiver group was added to the model.
     * @param e Event containing a reference to the newly added group.
     */
    public void receiverGroupAdded(ReceiverAddedOrRemovedEvent e) {
        try {
            ((ReceiverTableModel)getModel()).receiverGroupAdded(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }



    /**
     * Notify the table that a receiver group was removed from the model.
     * @param e Event containing a reference to the deleted group.
     */
    public void receiverGroupRemoved(ReceiverAddedOrRemovedEvent e) {
        try {
            ((ReceiverTableModel)getModel()).receiverGroupRemoved(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }



    /**
     * Notify the table that the data of contained receiver groups or receivers
     * has changed.
     * @param e Event containing the updated data and a reference to its source
     */
    public void dataChanged(ReceiverDataChangedEvent e) {
        try {
            ((ReceiverTableModel)getModel()).dataChanged(e);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }



    /**
     * Returns the ReceiverGroups whose rows are currently selected. Returns
     * an empty list, if no row is selected or if the selected rows only contain
     * Receivers.
     * @return List of selected ReceiverGroups, or the empty list if there are
     *         none.
     * @throws IllegalStateException if the table's model is no ReceiverTableModel
     */
    public List<ReceiverGroup> getSelectedReceiverGroups() {
        Set<ReceiverGroup> groupSet = new HashSet<ReceiverGroup>();
        if(dataModel instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)dataModel;
            for(int i : getSelectedRows()) {
                ReceiverGroup g = model.getReceiverGroupAt(i);
                if(g != null) {
                    groupSet.add(g);
                }
            }
            return new ArrayList<ReceiverGroup>(groupSet);
        } else {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }



    /**
     * Returns the Receivers whose rows are currently selected. Returns an empty
     * list, if no row is selected or if the selected rows only contain ReceiverGroups.
     * 
     * @return List of selected Receivers, or the empty list if there are none.
     * @throws IllegalStateException if the table's model is no ReceiverTableModel
     */
    public List<Receiver> getSelectedReceivers() {
        List<Receiver> receiverList = new ArrayList<Receiver>();
        if(dataModel instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)dataModel;
            for(int i : getSelectedRows()) {
                Receiver rcv = model.getReceiverAt(i);
                // if a null is returned, this row contained a ReceiverGroup
                if(rcv != null) {
                    receiverList.add(rcv);
                }
            }
            return receiverList;
        } else {
            throw new IllegalStateException("JReceiverTable must have a ReceiverTableModel but contains " + getModel().getClass());
        }
    }



    /**
     * Get the ReceiverGroup containing the specified Receiver. The containment
     * is determined by parent relations of rows in the table.
     *
     * @param r the Receiver who's parent is to be determined
     * @return the parent of the specified Receiver
     * @throws IllegalStateException if the table's model is no ReceiverTableModel
     */
    public ReceiverGroup getParent(Receiver r) {
        if(this.getModel() instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)getModel();
            return model.getParent(r);
        } else {
            throw new IllegalStateException("JReceiverTable must contain a ReceiverTableModel!");
        }
    }



    /**
     * Notify the table that a Receiver was removed from its ReceiverGroup
     *
     * @param r the receiver removed from its group
     * @throws IllegalStateException if the table's model is no ReceiverTableModel
     */
    public void receiverRemoved(Receiver r) {
        if(this.getModel() instanceof ReceiverTableModel) {
            ReceiverTableModel model = (ReceiverTableModel)getModel();
            model.receiverRemoved(r);
        } else {
            throw new IllegalStateException("JReceiverTable must contain a ReceiverTableModel!");
        }
    }



    /**
     * Proxy method to minimize the chances, that the table will be fed a
     * non-ReceiverTableModel as data model. Unfortunately won't prevent this
     * during compile time but only at runtime.
     * @param dataModel
     */
    @Override
    public void setModel(TableModel dataModel) {
        if(dataModel instanceof ReceiverTableModel) {
            super.setModel(dataModel);
        } else {
            throw new IllegalArgumentException("Illegal data model type: " + dataModel.getClass().getCanonicalName() + ". Must be of type ReceiverTableModel.");
        }
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = columnModel.getColumn(index).getModelIndex();
                return ReceiverTableColumn.values()[realIndex].getToolTip();
            }
        };
    }

}
