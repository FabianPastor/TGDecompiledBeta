package org.telegram.ui;

import org.telegram.messenger.MessagesController;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ CharSequence f$1;
    public final /* synthetic */ MessagesController f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda13(ChatActivity chatActivity, CharSequence charSequence, MessagesController messagesController, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = charSequence;
        this.f$2 = messagesController;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m1792lambda$searchLinks$83$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
