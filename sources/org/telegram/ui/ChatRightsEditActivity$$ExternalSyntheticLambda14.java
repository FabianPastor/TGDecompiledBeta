package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_channels_editCreator;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda14 implements RequestDelegate {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC$InputCheckPasswordSRP f$1;
    public final /* synthetic */ TwoStepVerificationActivity f$2;
    public final /* synthetic */ TLRPC$TL_channels_editCreator f$3;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda14(ChatRightsEditActivity chatRightsEditActivity, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_channels_editCreator tLRPC$TL_channels_editCreator) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tLRPC$InputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
        this.f$3 = tLRPC$TL_channels_editCreator;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$initTransfer$14(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
