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

    STATUS          ("MainFrame.ReceiverTable.status"),
    /**
     * Column representing the id of the sender of the stream received by a
     * receiver.
     */
    SENDER_ID       ("MainFrame.ReceiverTable.sender_id"),

    /**
     * Column representing the ip address of the sender of the stream received
     * by a receiver.
     */
    SENDER_IP       ("MainFrame.ReceiverTable.sender_ip"),

    /**
     * Column representing the packet rate as configured at the sending side
     * of the stream received by a receiver.
     */
    SENDER_CONF_PPS ("MainFrame.ReceiverTable.sender_conf_pps"),

    /**
     * Column representing the packet reate as measured at the receiving side.
     */
    AVG_PPS         ("MainFrame.ReceiverTable.measured_pps"),

    /**
     * Column representing the count of lost packets for one stream.
     */
    LOST_PACKETS    ("MainFrame.ReceiverTable.lost_packets"),

    /**
     * Column representing the payload that is shipped with a multicast stream.
     */
    PAYLOAD         ("MainFrame.ReceiverTable.payload");

    /**
     * The identifier of the internationalized caption of a column.
     */
    private String captionIdentifier;

    /**
     * Initializes a column type with the specified identifier of its
     * internationlized caption.
     * @param captionIdentifier resource identifier of the internationalized
     * caption of this column
     */
    ReceiverTableColumn(String captionIdentifier) {
        this.captionIdentifier = captionIdentifier;
    }

    /**
     * Get the internationlized caption for this column.
     * @return the internationalize caption for this column
     */
    String getCaption() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(captionIdentifier);
    }
}
