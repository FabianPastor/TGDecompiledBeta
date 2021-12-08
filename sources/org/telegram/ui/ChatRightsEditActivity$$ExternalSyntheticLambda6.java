package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TwoStepVerificationActivity f$1;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda6(ChatRightsEditActivity chatRightsEditActivity, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = twoStepVerificationActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1951lambda$initTransfer$12$orgtelegramuiChatRightsEditActivity(this.f$1, tLObject, tL_error);
    }
}
