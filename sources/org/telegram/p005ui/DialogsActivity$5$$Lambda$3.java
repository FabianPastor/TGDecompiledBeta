package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.DialogsActivity.C14325;

/* renamed from: org.telegram.ui.DialogsActivity$5$$Lambda$3 */
final /* synthetic */ class DialogsActivity$5$$Lambda$3 implements OnClickListener {
    private final C14325 arg$1;
    private final int arg$2;
    private final boolean arg$3;
    private final boolean arg$4;

    DialogsActivity$5$$Lambda$3(C14325 c14325, int i, boolean z, boolean z2) {
        this.arg$1 = c14325;
        this.arg$2 = i;
        this.arg$3 = z;
        this.arg$4 = z2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$4$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
