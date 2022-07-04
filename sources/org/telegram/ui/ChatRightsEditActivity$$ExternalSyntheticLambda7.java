package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.InputCheckPasswordSRP f$2;
    public final /* synthetic */ TwoStepVerificationActivity f$3;
    public final /* synthetic */ TLRPC.TL_channels_editCreator f$4;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda7(ChatRightsEditActivity chatRightsEditActivity, TLRPC.TL_error tL_error, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.TL_channels_editCreator tL_channels_editCreator) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tL_error;
        this.f$2 = inputCheckPasswordSRP;
        this.f$3 = twoStepVerificationActivity;
        this.f$4 = tL_channels_editCreator;
    }

    public final void run() {
        this.f$0.m3282lambda$initTransfer$13$orgtelegramuiChatRightsEditActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
