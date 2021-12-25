package org.telegram.ui;

import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda126 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda126(ChatActivity chatActivity, MessageObject messageObject, int i) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$96(this.f$1, this.f$2);
    }
}
