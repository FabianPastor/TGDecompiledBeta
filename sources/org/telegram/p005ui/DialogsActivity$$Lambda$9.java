package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.DialogsActivity$$Lambda$9 */
final /* synthetic */ class DialogsActivity$$Lambda$9 implements OnClickListener {
    private final DialogsActivity arg$1;
    private final long arg$2;

    DialogsActivity$$Lambda$9(DialogsActivity dialogsActivity, long j) {
        this.arg$1 = dialogsActivity;
        this.arg$2 = j;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didSelectResult$9$DialogsActivity(this.arg$2, dialogInterface, i);
    }
}
