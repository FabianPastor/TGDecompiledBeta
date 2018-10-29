package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$23 implements OnClickListener {
    private final TwoStepVerificationActivity arg$1;
    private final TL_account_updatePasswordSettings arg$2;

    TwoStepVerificationActivity$$Lambda$23(TwoStepVerificationActivity twoStepVerificationActivity, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = tL_account_updatePasswordSettings;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$17$TwoStepVerificationActivity(this.arg$2, dialogInterface, i);
    }
}
