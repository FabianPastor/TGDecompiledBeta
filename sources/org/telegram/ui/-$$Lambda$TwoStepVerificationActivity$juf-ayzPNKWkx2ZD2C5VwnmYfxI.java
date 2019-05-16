package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TwoStepVerificationActivity$juf-ayzPNKWkx2ZD2C5VwnmYfxI implements RequestDelegate {
    private final /* synthetic */ TwoStepVerificationActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$TwoStepVerificationActivity$juf-ayzPNKWkx2ZD2C5VwnmYfxI(TwoStepVerificationActivity twoStepVerificationActivity, boolean z) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$20$TwoStepVerificationActivity(this.f$1, tLObject, tL_error);
    }
}
