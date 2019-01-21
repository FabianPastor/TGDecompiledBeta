package org.telegram.ui.ActionBar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertDialog$$Lambda$5 implements OnClickListener {
    private final AlertDialog arg$1;

    AlertDialog$$Lambda$5(AlertDialog alertDialog) {
        this.arg$1 = alertDialog;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showCancelAlert$4$AlertDialog(dialogInterface, i);
    }
}
