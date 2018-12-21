package org.telegram.p005ui;

import android.os.Bundle;
import org.telegram.p005ui.ActionBar.BaseFragment;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$41 */
final /* synthetic */ class ChatActivity$$Lambda$41 implements Runnable {
    private final ChatActivity arg$1;
    private final BaseFragment arg$2;
    private final Bundle arg$3;
    private final int arg$4;

    ChatActivity$$Lambda$41(ChatActivity chatActivity, BaseFragment baseFragment, Bundle bundle, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = baseFragment;
        this.arg$3 = bundle;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$54$ChatActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
