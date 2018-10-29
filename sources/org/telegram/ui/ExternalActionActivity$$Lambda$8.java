package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ExternalActionActivity$$Lambda$8 implements OnDismissListener {
    private final ExternalActionActivity arg$1;
    private final TL_error arg$2;

    ExternalActionActivity$$Lambda$8(ExternalActionActivity externalActionActivity, TL_error tL_error) {
        this.arg$1 = externalActionActivity;
        this.arg$2 = tL_error;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$null$7$ExternalActionActivity(this.arg$2, dialogInterface);
    }
}
