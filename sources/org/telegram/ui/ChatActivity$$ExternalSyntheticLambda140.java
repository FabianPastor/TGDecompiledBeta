package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda140 implements MessagesStorage.IntCallback {
    public final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda140(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void run(int i) {
        this.f$0.jumpToDate(i);
    }
}
