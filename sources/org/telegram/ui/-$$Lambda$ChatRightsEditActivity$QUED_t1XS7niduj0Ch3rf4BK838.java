package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$QUED_t1XS7niduj0Ch3rf4BK838 implements RequestDelegate {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ InputCheckPasswordSRP f$1;
    private final /* synthetic */ TwoStepVerificationActivity f$2;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$QUED_t1XS7niduj0Ch3rf4BK838(ChatRightsEditActivity chatRightsEditActivity, InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = inputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$initTransfer$14$ChatRightsEditActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
