package org.telegram.ui;

import org.telegram.messenger.MessagesController;

final /* synthetic */ class ChatActivity$$Lambda$33 implements Runnable {
    private final ChatActivity arg$1;
    private final CharSequence arg$2;
    private final MessagesController arg$3;
    private final boolean arg$4;

    ChatActivity$$Lambda$33(ChatActivity chatActivity, CharSequence charSequence, MessagesController messagesController, boolean z) {
        this.arg$1 = chatActivity;
        this.arg$2 = charSequence;
        this.arg$3 = messagesController;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$searchLinks$46$ChatActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
