package org.telegram.messenger;

import org.telegram.p005ui.ChatActivity;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$7 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final String arg$2;
    private final boolean arg$3;
    private final MessageObject arg$4;
    private final KeyboardButton arg$5;
    private final ChatActivity arg$6;

    SendMessagesHelper$$Lambda$7(SendMessagesHelper sendMessagesHelper, String str, boolean z, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = str;
        this.arg$3 = z;
        this.arg$4 = messageObject;
        this.arg$5 = keyboardButton;
        this.arg$6 = chatActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendCallback$18$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, tLObject, tL_error);
    }
}
