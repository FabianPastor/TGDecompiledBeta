package org.telegram.p005ui;

import org.telegram.tgnet.TLRPC.Updates;

/* renamed from: org.telegram.ui.ChannelUsersActivity$$Lambda$11 */
final /* synthetic */ class ChannelUsersActivity$$Lambda$11 implements Runnable {
    private final ChannelUsersActivity arg$1;
    private final Updates arg$2;

    ChannelUsersActivity$$Lambda$11(ChannelUsersActivity channelUsersActivity, Updates updates) {
        this.arg$1 = channelUsersActivity;
        this.arg$2 = updates;
    }

    public void run() {
        this.arg$1.lambda$null$7$ChannelUsersActivity(this.arg$2);
    }
}
