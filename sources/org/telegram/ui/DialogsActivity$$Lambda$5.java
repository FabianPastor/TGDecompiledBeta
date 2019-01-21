package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class DialogsActivity$$Lambda$5 implements OnClickListener {
    private final DialogsActivity arg$1;

    DialogsActivity$$Lambda$5(DialogsActivity dialogsActivity) {
        this.arg$1 = dialogsActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onResume$5$DialogsActivity(dialogInterface, i);
    }
}
