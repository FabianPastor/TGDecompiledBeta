package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$22 implements OnClickListener {
    private final TwoStepVerificationActivity arg$1;
    private final byte[] arg$2;
    private final TL_account_updatePasswordSettings arg$3;

    TwoStepVerificationActivity$$Lambda$22(TwoStepVerificationActivity twoStepVerificationActivity, byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = bArr;
        this.arg$3 = tL_account_updatePasswordSettings;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$16$TwoStepVerificationActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
