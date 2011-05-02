/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

import java.util.ResourceBundle;

/**
 *
 * @author Tobias
 */
public enum ReceiverTableColumn {
    STATUS          ("MainFrame.ReceiverTable.status"),
    SENDER_ID       ("MainFrame.ReceiverTable.sender_id"),
    SENDER_IP       ("MainFrame.ReceiverTable.sender_ip"),
    SENDER_CONF_PPS ("MainFrame.ReceiverTable.sender_conf_pps"),
    AVG_PPS         ("MainFrame.ReceiverTable.measured_pps"),
    LOST_PACKETS    ("MainFrame.ReceiverTable.lost_packets"),
    PAYLOAD         ("MainFrame.ReceiverTable.payload");

    private String captionIdentifier;

    ReceiverTableColumn(String captionIdentifier) {
        this.captionIdentifier = captionIdentifier;
    }

    String getCaption() {
        return ResourceBundle.getBundle("internationalization/Bundle").getString(captionIdentifier);
    }
}
