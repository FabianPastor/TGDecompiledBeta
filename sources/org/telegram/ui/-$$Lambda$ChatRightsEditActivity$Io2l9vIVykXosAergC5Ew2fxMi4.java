package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$Io2l9vIVykXosAergC5Ew2fxMi4 implements Runnable {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TwoStepVerificationActivity f$3;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$Io2l9vIVykXosAergC5Ew2fxMi4(ChatRightsEditActivity chatRightsEditActivity, TL_error tL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = twoStepVerificationActivity;
    }

    public final void run() {
        this.f$0.lambda$null$11$ChatRightsEditActivity(this.f$1, this.f$2, this.f$3);
    }
}
