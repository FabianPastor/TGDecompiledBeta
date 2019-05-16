package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TwoStepVerificationActivity$BbXpzRMc1z5d7yEqTJj2C6ReLvg implements Runnable {
    private final /* synthetic */ TwoStepVerificationActivity f$0;
    private final /* synthetic */ TL_account_updatePasswordSettings f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$TwoStepVerificationActivity$BbXpzRMc1z5d7yEqTJj2C6ReLvg(TwoStepVerificationActivity twoStepVerificationActivity, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, boolean z, String str) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tL_account_updatePasswordSettings;
        this.f$2 = z;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$setNewPassword$25$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
    }
}
