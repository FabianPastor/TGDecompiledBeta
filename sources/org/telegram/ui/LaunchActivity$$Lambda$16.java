package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

final /* synthetic */ class LaunchActivity$$Lambda$16 implements PhonebookSelectActivityDelegate {
    private final LaunchActivity arg$1;
    private final ChatActivity arg$2;
    private final int arg$3;
    private final long arg$4;

    LaunchActivity$$Lambda$16(LaunchActivity launchActivity, ChatActivity chatActivity, int i, long j) {
        this.arg$1 = launchActivity;
        this.arg$2 = chatActivity;
        this.arg$3 = i;
        this.arg$4 = j;
    }

    public void didSelectContact(User user) {
        this.arg$1.lambda$didSelectDialogs$28$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, user);
    }
}
