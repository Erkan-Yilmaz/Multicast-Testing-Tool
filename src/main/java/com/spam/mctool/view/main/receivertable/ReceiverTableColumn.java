/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.receivertable;

/**
 *
 * @author Tobias
 */
public enum ReceiverTableColumn {
    STATUS          ("Status"),
    SENDER_ID       ("Sender ID"),
    SENDER_IP       ("Sender IP"),
    SENDER_CONF_PPS ("Conf. Packet Rate"),
    AVG_PPS         ("Measured Packet Rate"),
    LOST_PACKETS    ("Lost Packets"),
    PAYLOAD         ("Payload");

    private String caption;

    ReceiverTableColumn(String caption) {
        this.caption = caption;
    }

    String getCaption() {
        return caption;
    }
}
