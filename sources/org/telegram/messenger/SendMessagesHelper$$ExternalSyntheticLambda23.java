package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.TwoStepVerificationActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.InputCheckPasswordSRP f$10;
    public final /* synthetic */ boolean f$11;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ TLRPC.KeyboardButton f$5;
    public final /* synthetic */ ChatActivity f$6;
    public final /* synthetic */ TwoStepVerificationActivity f$7;
    public final /* synthetic */ TLObject[] f$8;
    public final /* synthetic */ TLRPC.TL_error f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda23(SendMessagesHelper sendMessagesHelper, String str, boolean z, TLObject tLObject, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, ChatActivity chatActivity, TwoStepVerificationActivity twoStepVerificationActivity, TLObject[] tLObjectArr, TLRPC.TL_error tL_error, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, boolean z2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = messageObject;
        this.f$5 = keyboardButton;
        this.f$6 = chatActivity;
        this.f$7 = twoStepVerificationActivity;
        this.f$8 = tLObjectArr;
        this.f$9 = tL_error;
        this.f$10 = inputCheckPasswordSRP;
        this.f$11 = z2;
    }

    public final void run() {
        this.f$0.m475lambda$sendCallback$29$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
    }
}
