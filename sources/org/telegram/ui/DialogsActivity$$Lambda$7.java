package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class DialogsActivity$$Lambda$7 implements OnClickListener {
    private final DialogsActivity arg$1;
    private final long arg$2;

    DialogsActivity$$Lambda$7(DialogsActivity dialogsActivity, long j) {
        this.arg$1 = dialogsActivity;
        this.arg$2 = j;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didSelectResult$7$DialogsActivity(this.arg$2, dialogInterface, i);
    }
}
