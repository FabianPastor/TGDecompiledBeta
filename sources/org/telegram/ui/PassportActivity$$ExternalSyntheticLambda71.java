package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda71 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC.TL_auth_passwordRecovery f$1;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda71(PassportActivity passportActivity, TLRPC.TL_auth_passwordRecovery tL_auth_passwordRecovery) {
        this.f$0 = passportActivity;
        this.f$1 = tL_auth_passwordRecovery;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m4068xc6f2e982(this.f$1, dialogInterface, i);
    }
}
