package org.telegram.ui;

import org.telegram.messenger.MessagesController;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessagesController f$1;
    public final /* synthetic */ CharSequence f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda23(ChatActivity chatActivity, MessagesController messagesController, CharSequence charSequence, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = messagesController;
        this.f$2 = charSequence;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m1789lambda$searchLinks$80$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
