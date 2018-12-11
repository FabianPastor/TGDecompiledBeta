package org.telegram.p005ui.ActionBar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* renamed from: org.telegram.ui.ActionBar.AlertDialog$$Lambda$6 */
final /* synthetic */ class AlertDialog$$Lambda$6 implements OnDismissListener {
    private final AlertDialog arg$1;

    AlertDialog$$Lambda$6(AlertDialog alertDialog) {
        this.arg$1 = alertDialog;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$showCancelAlert$5$AlertDialog(dialogInterface);
    }
}
