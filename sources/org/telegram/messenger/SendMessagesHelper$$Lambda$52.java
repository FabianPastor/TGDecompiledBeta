package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.ChatActivity;

final /* synthetic */ class SendMessagesHelper$$Lambda$52 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final String arg$2;
    private final boolean arg$3;
    private final TLObject arg$4;
    private final MessageObject arg$5;
    private final KeyboardButton arg$6;
    private final ChatActivity arg$7;

    SendMessagesHelper$$Lambda$52(SendMessagesHelper sendMessagesHelper, String str, boolean z, TLObject tLObject, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = str;
        this.arg$3 = z;
        this.arg$4 = tLObject;
        this.arg$5 = messageObject;
        this.arg$6 = keyboardButton;
        this.arg$7 = chatActivity;
    }

    public void run() {
        this.arg$1.lambda$null$15$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
