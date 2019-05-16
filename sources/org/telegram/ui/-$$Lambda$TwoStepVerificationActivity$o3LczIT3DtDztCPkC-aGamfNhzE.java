package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TwoStepVerificationActivity$o3LczIT3DtDztCPkC-aGamfNhzE implements OnClickListener {
    private final /* synthetic */ TwoStepVerificationActivity f$0;
    private final /* synthetic */ TL_auth_passwordRecovery f$1;

    public /* synthetic */ -$$Lambda$TwoStepVerificationActivity$o3LczIT3DtDztCPkC-aGamfNhzE(TwoStepVerificationActivity twoStepVerificationActivity, TL_auth_passwordRecovery tL_auth_passwordRecovery) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tL_auth_passwordRecovery;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$1$TwoStepVerificationActivity(this.f$1, dialogInterface, i);
    }
}
