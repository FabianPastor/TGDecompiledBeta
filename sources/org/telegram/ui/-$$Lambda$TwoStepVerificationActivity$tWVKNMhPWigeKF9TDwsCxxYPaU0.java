package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TwoStepVerificationActivity$tWVKNMhPWigeKF9TDwsCxxYPaU0 implements Runnable {
    private final /* synthetic */ TwoStepVerificationActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ byte[] f$4;
    private final /* synthetic */ TL_account_updatePasswordSettings f$5;
    private final /* synthetic */ String f$6;

    public /* synthetic */ -$$Lambda$TwoStepVerificationActivity$tWVKNMhPWigeKF9TDwsCxxYPaU0(TwoStepVerificationActivity twoStepVerificationActivity, TL_error tL_error, boolean z, TLObject tLObject, byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, String str) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tL_error;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = bArr;
        this.f$5 = tL_account_updatePasswordSettings;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.lambda$null$23$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
