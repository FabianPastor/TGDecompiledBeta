package org.telegram.ui;

import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda111 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda111(ChatActivity chatActivity, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$updateMessagesVisiblePart$79(this.f$1);
    }
}