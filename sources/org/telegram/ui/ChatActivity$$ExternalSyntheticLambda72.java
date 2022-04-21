package org.telegram.ui;

import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda72 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda72(ChatActivity chatActivity, MessageObject messageObject, int i) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m1727lambda$didReceivedNotification$115$orgtelegramuiChatActivity(this.f$1, this.f$2);
    }
}
