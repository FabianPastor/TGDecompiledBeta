package org.telegram.p005ui;

/* renamed from: org.telegram.ui.ChannelCreateActivity$$Lambda$10 */
final /* synthetic */ class ChannelCreateActivity$$Lambda$10 implements Runnable {
    private final ChannelCreateActivity arg$1;
    private final String arg$2;

    ChannelCreateActivity$$Lambda$10(ChannelCreateActivity channelCreateActivity, String str) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$checkUserName$20$ChannelCreateActivity(this.arg$2);
    }
}
