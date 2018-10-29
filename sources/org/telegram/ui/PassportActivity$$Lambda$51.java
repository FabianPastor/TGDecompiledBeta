package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

final /* synthetic */ class PassportActivity$$Lambda$51 implements OnClickListener {
    private final PassportActivity arg$1;
    private final TL_secureRequiredType arg$2;
    private final boolean arg$3;

    PassportActivity$$Lambda$51(PassportActivity passportActivity, TL_secureRequiredType tL_secureRequiredType, boolean z) {
        this.arg$1 = passportActivity;
        this.arg$2 = tL_secureRequiredType;
        this.arg$3 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$63$PassportActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
