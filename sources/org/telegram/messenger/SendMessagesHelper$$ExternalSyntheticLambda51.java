package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.TwoStepVerificationActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda51 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TwoStepVerificationActivity f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ TLRPC.KeyboardButton f$6;
    public final /* synthetic */ ChatActivity f$7;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda51(SendMessagesHelper sendMessagesHelper, TLRPC.TL_error tL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, ChatActivity chatActivity) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = twoStepVerificationActivity;
        this.f$4 = z;
        this.f$5 = messageObject;
        this.f$6 = keyboardButton;
        this.f$7 = chatActivity;
    }

    public final void run() {
        this.f$0.m462lambda$sendCallback$27$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
