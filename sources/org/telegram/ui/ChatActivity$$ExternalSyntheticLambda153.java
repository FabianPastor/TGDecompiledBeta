package org.telegram.ui;

import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda153 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda153(ChatActivity chatActivity, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$unpinMessage$164(this.f$1);
    }
}
