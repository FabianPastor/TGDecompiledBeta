package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TwoStepVerificationActivity$VJV5wRfpqB6jJ8dEtoqePT3HaYk implements RequestDelegate {
    private final /* synthetic */ TwoStepVerificationActivity f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ byte[] f$2;
    private final /* synthetic */ TL_account_updatePasswordSettings f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$TwoStepVerificationActivity$VJV5wRfpqB6jJ8dEtoqePT3HaYk(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, String str) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = z;
        this.f$2 = bArr;
        this.f$3 = tL_account_updatePasswordSettings;
        this.f$4 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$24$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
