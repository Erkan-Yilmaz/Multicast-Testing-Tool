/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spam.mctool.view.main.sendertable;

/**
 *
 * @author Tobias
 */
public enum SenderTableColumn {
    STATUS        ("Status"),
    SENDER_ID     ("Sender ID"),
    PORT          ("Port"),
    GROUP_ADDRESS ("Multicast Group"),
    CONF_PPS      ("Conf. Packet Rate"),
    AVG_PPS       ("Measured Packet Rate"),
    MIN_PPS       ("Min. Packet Rate"),
    MAX_PPS       ("Max. Packet Rate");

    private String caption;

    SenderTableColumn(String caption) {
        this.caption = caption;
    }

    String getCaption() {
        return caption;
    }
}
