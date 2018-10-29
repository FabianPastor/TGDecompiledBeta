package org.telegram.ui;

import android.os.Bundle;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class ChatActivity$$Lambda$39 implements Runnable {
    private final ChatActivity arg$1;
    private final BaseFragment arg$2;
    private final Bundle arg$3;
    private final int arg$4;

    ChatActivity$$Lambda$39(ChatActivity chatActivity, BaseFragment baseFragment, Bundle bundle, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = baseFragment;
        this.arg$3 = bundle;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$51$ChatActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
