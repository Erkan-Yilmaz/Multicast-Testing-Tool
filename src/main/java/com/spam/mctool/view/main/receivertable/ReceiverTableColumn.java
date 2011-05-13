/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import java.util.ResourceBundle;

/**
 * Defines the columns and its' captions as displayed in the <code>JReceiverTable</code>.
 * @author Tobias Stöckel
 */
public enum ReceiverTableColumn {

    /**
     * Column representing the current status of a receiver.
     */

    STATUS          ("MainFrame.ReceiverTable.status", "MainFrame.ReceiverTable.status.tooltip"),
    /**
     * Column representing the id of the sender of the stream received by a
     * receiver.
     */
    SENDER_ID       ("MainFrame.ReceiverTable.sender_id", "MainFrame.ReceiverTable.sender_id.tooltip"),

    /**
     * Column representing the ip address of the sender of the stream received
     * by a receiver.
     */
    SENDER_IP       ("MainFrame.ReceiverTable.sender_ip", "MainFrame.ReceiverTable.sender_ip.tooltip"),

    /**
     * Column representing the packet rate as configured at the sending side
     * of the stream received by a receiver.
     */
    SENDER_CONF_PPS ("MainFrame.ReceiverTable.sender_conf_pps", "MainFrame.ReceiverTable.sender_conf_pps.tooltip"),

    /**
     * Column representing the packet reate as measured at the receiving side.
     */
    AVG_PPS         ("MainFrame.ReceiverTable.measured_pps", "MainFrame.ReceiverTable.measured_pps.tooltip"),

    /**
     * Column representing the count of lost packets for one stream.
     */
    LOST_PACKETS    ("MainFrame.ReceiverTable.lost_packets", "MainFrame.ReceiverTable.lost_packets.tooltip"),

    /**
     * Column representing the payload that is shipped with a multicast stream.
     */
    PAYLOAD         ("MainFrame.ReceiverTable.payload", "MainFrame.ReceiverTable.payload.tooltip");

    /**
     * The identifier of the internationalized caption of a column.
     */
    private String captionIdentifier;

    /**
     * The identifier of the internationalized tooltip of a column.
     */
    private String tooltipIdentifier;

    /**
     * Initializes a column type with the specified identifiers of its
     * internationlized caption and tooltip.
     * @param captionIdentifier resource identifier of the internationalized
     * caption of this column
     * @param tooltipIdentifier resource identifier of the internationalized
     * tooltip of this column
     */
    ReceiverTableColumn(String captionIdentifier, String tooltipIdentifier) {
        this.captionIdentifier = captionIdentifier;
        this.tooltipIdentifier = tooltipIdentifier;
    }

    /**
     * Get the internationlized caption for this column.
     * @return the internationalize caption for this column
     */
    String getCaption() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(captionIdentifier);
    }

    /**
     * Get the internationlized tooltip for this column.
     * @return the internationalize tooltip for this column
     */
    String getToolTip() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(tooltipIdentifier);
    }
}
