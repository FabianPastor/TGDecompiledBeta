package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$EmEFuBPyDzxsPVJjrp4s9BS_M7o implements RequestDelegate {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ TwoStepVerificationActivity f$1;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$EmEFuBPyDzxsPVJjrp4s9BS_M7o(ChatRightsEditActivity chatRightsEditActivity, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = twoStepVerificationActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$12$ChatRightsEditActivity(this.f$1, tLObject, tL_error);
    }
}
