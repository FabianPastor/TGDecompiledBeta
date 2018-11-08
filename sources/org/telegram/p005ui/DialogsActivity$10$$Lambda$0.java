package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.DialogsActivity.C210410;

/* renamed from: org.telegram.ui.DialogsActivity$10$$Lambda$0 */
final /* synthetic */ class DialogsActivity$10$$Lambda$0 implements OnClickListener {
    private final C210410 arg$1;
    private final int arg$2;

    DialogsActivity$10$$Lambda$0(C210410 c210410, int i) {
        this.arg$1 = c210410;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$needRemoveHint$0$DialogsActivity$10(this.arg$2, dialogInterface, i);
    }
}
