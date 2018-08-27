package org.telegram.ui;

import org.telegram.messenger.MessagesController;

final /* synthetic */ class ChatActivity$$Lambda$73 implements Runnable {
    private final ChatActivity arg$1;
    private final MessagesController arg$2;
    private final CharSequence arg$3;
    private final boolean arg$4;

    ChatActivity$$Lambda$73(ChatActivity chatActivity, MessagesController messagesController, CharSequence charSequence, boolean z) {
        this.arg$1 = chatActivity;
        this.arg$2 = messagesController;
        this.arg$3 = charSequence;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$null$43$ChatActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
