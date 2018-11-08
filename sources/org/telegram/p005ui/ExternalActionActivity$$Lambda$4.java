package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* renamed from: org.telegram.ui.ExternalActionActivity$$Lambda$4 */
final /* synthetic */ class ExternalActionActivity$$Lambda$4 implements OnDismissListener {
    private final ExternalActionActivity arg$1;

    ExternalActionActivity$$Lambda$4(ExternalActionActivity externalActionActivity) {
        this.arg$1 = externalActionActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$handleIntent$4$ExternalActionActivity(dialogInterface);
    }
}
