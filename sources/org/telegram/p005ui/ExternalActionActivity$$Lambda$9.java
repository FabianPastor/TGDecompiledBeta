package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ExternalActionActivity$$Lambda$9 */
final /* synthetic */ class ExternalActionActivity$$Lambda$9 implements OnDismissListener {
    private final ExternalActionActivity arg$1;
    private final TL_error arg$2;

    ExternalActionActivity$$Lambda$9(ExternalActionActivity externalActionActivity, TL_error tL_error) {
        this.arg$1 = externalActionActivity;
        this.arg$2 = tL_error;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$null$8$ExternalActionActivity(this.arg$2, dialogInterface);
    }
}
