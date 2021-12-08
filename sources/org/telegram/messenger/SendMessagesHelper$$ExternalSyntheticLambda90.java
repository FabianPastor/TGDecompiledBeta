package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.TwoStepVerificationActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda90 implements TwoStepVerificationActivity.TwoStepVerificationActivityDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ TLRPC.KeyboardButton f$3;
    public final /* synthetic */ TwoStepVerificationActivity f$4;
    public final /* synthetic */ ChatActivity f$5;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda90(SendMessagesHelper sendMessagesHelper, boolean z, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, TwoStepVerificationActivity twoStepVerificationActivity, ChatActivity chatActivity) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = z;
        this.f$2 = messageObject;
        this.f$3 = keyboardButton;
        this.f$4 = twoStepVerificationActivity;
        this.f$5 = chatActivity;
    }

    public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        this.f$0.m460lambda$sendCallback$24$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, inputCheckPasswordSRP);
    }
}
