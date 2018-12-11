package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ChannelEditInfoActivity$$Lambda$12 */
final /* synthetic */ class ChannelEditInfoActivity$$Lambda$12 implements Runnable {
    private final ChannelEditInfoActivity arg$1;
    private final TLObject arg$2;

    ChannelEditInfoActivity$$Lambda$12(ChannelEditInfoActivity channelEditInfoActivity, TLObject tLObject) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$10$ChannelEditInfoActivity(this.arg$2);
    }
}
