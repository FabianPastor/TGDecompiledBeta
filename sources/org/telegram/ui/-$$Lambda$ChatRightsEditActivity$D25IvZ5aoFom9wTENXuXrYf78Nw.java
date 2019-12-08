package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC.TL_channels_editCreator;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$D25IvZ5aoFom9wTENXuXrYvar_Nw implements RequestDelegate {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ InputCheckPasswordSRP f$1;
    private final /* synthetic */ TwoStepVerificationActivity f$2;
    private final /* synthetic */ TL_channels_editCreator f$3;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$D25IvZ5aoFom9wTENXuXrYvar_Nw(ChatRightsEditActivity chatRightsEditActivity, InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TL_channels_editCreator tL_channels_editCreator) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = inputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
        this.f$3 = tL_channels_editCreator;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$initTransfer$14$ChatRightsEditActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
