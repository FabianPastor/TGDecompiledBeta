package org.telegram.ui;

import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda169 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ ChatMessageCell f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda169(ChatActivity chatActivity, ChatMessageCell chatMessageCell) {
        this.f$0 = chatActivity;
        this.f$1 = chatMessageCell;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$112(this.f$1);
    }
}
