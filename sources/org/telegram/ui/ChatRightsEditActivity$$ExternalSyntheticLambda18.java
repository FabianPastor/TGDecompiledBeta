package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TwoStepVerificationActivity f$3;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda18(ChatRightsEditActivity chatRightsEditActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = twoStepVerificationActivity;
    }

    public final void run() {
        this.f$0.lambda$initTransfer$11(this.f$1, this.f$2, this.f$3);
    }
}
