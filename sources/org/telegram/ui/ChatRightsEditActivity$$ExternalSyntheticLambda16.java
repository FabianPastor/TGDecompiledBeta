package org.telegram.ui;

import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_channels_editCreator;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$InputCheckPasswordSRP f$2;
    public final /* synthetic */ TwoStepVerificationActivity f$3;
    public final /* synthetic */ TLRPC$TL_channels_editCreator f$4;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda16(ChatRightsEditActivity chatRightsEditActivity, TLRPC$TL_error tLRPC$TL_error, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_channels_editCreator tLRPC$TL_channels_editCreator) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$InputCheckPasswordSRP;
        this.f$3 = twoStepVerificationActivity;
        this.f$4 = tLRPC$TL_channels_editCreator;
    }

    public final void run() {
        this.f$0.lambda$initTransfer$13(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
