package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ChannelCreateActivity$$Lambda$14 */
final /* synthetic */ class ChannelCreateActivity$$Lambda$14 implements Runnable {
    private final ChannelCreateActivity arg$1;
    private final TLObject arg$2;

    ChannelCreateActivity$$Lambda$14(ChannelCreateActivity channelCreateActivity, TLObject tLObject) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$16$ChannelCreateActivity(this.arg$2);
    }
}
