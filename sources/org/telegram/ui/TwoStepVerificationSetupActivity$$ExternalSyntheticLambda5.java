package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ byte[] f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, byte[] bArr) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = bArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$setNewPassword$32(this.f$1, dialogInterface, i);
    }
}
