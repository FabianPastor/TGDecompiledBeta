package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda154 implements MessagesStorage.IntCallback {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda154(ChatActivity chatActivity, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = z;
    }

    public final void run(int i) {
        this.f$0.lambda$processSelectedOption$137(this.f$1, i);
    }
}
