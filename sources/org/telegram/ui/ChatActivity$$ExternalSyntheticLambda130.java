package org.telegram.ui;

import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda130 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda130(ChatActivity chatActivity, MessageObject messageObject, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$updateMessageAnimated$140(this.f$1, this.f$2);
    }
}
