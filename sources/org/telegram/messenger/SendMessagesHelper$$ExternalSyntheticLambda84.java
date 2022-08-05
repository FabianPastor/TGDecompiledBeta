package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.TwoStepVerificationActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda84 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ TLRPC$KeyboardButton f$4;
    public final /* synthetic */ ChatActivity f$5;
    public final /* synthetic */ TwoStepVerificationActivity f$6;
    public final /* synthetic */ TLObject[] f$7;
    public final /* synthetic */ TLRPC$InputCheckPasswordSRP f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda84(SendMessagesHelper sendMessagesHelper, String str, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TwoStepVerificationActivity twoStepVerificationActivity, TLObject[] tLObjectArr, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, boolean z2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = messageObject;
        this.f$4 = tLRPC$KeyboardButton;
        this.f$5 = chatActivity;
        this.f$6 = twoStepVerificationActivity;
        this.f$7 = tLObjectArr;
        this.f$8 = tLRPC$InputCheckPasswordSRP;
        this.f$9 = z2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendCallback$30(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tLRPC$TL_error);
    }
}
