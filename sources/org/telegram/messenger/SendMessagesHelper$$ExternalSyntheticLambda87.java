package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.TwoStepVerificationActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda87 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TwoStepVerificationActivity f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ TLRPC.KeyboardButton f$4;
    public final /* synthetic */ ChatActivity f$5;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda87(SendMessagesHelper sendMessagesHelper, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, ChatActivity chatActivity) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = twoStepVerificationActivity;
        this.f$2 = z;
        this.f$3 = messageObject;
        this.f$4 = keyboardButton;
        this.f$5 = chatActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m474lambda$sendCallback$28$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
