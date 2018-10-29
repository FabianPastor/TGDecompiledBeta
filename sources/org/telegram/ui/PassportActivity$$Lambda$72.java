package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;

final /* synthetic */ class PassportActivity$$Lambda$72 implements OnClickListener {
    private final PassportActivity arg$1;
    private final TL_auth_passwordRecovery arg$2;

    PassportActivity$$Lambda$72(PassportActivity passportActivity, TL_auth_passwordRecovery tL_auth_passwordRecovery) {
        this.arg$1 = passportActivity;
        this.arg$2 = tL_auth_passwordRecovery;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$8$PassportActivity(this.arg$2, dialogInterface, i);
    }
}
