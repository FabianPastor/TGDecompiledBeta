package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ChatActivity.ChatActivityAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ ChatActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1(ChatActivity.ChatActivityAdapter.AnonymousClass1 r1, MessageObject messageObject) {
        this.f$0 = r1;
        this.f$1 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$didPressImage$6(this.f$1);
    }
}
