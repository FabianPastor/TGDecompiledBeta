package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda14 implements RequestDelegate {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC.InputCheckPasswordSRP f$1;
    public final /* synthetic */ TwoStepVerificationActivity f$2;
    public final /* synthetic */ TLRPC.TL_channels_editCreator f$3;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda14(ChatRightsEditActivity chatRightsEditActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.TL_channels_editCreator tL_channels_editCreator) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = inputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
        this.f$3 = tL_channels_editCreator;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3283lambda$initTransfer$14$orgtelegramuiChatRightsEditActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
