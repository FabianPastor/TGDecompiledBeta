package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;

/* renamed from: org.telegram.ui.TwoStepVerificationActivity$$Lambda$31 */
final /* synthetic */ class TwoStepVerificationActivity$$Lambda$31 implements OnClickListener {
    private final TwoStepVerificationActivity arg$1;
    private final TL_auth_passwordRecovery arg$2;

    TwoStepVerificationActivity$$Lambda$31(TwoStepVerificationActivity twoStepVerificationActivity, TL_auth_passwordRecovery tL_auth_passwordRecovery) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = tL_auth_passwordRecovery;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$TwoStepVerificationActivity(this.arg$2, dialogInterface, i);
    }
}
