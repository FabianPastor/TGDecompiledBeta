package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.DialogsActivity.CLASSNAME;

/* renamed from: org.telegram.ui.DialogsActivity$10$$Lambda$0 */
final /* synthetic */ class DialogsActivity$10$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final int arg$2;

    DialogsActivity$10$$Lambda$0(CLASSNAME CLASSNAME, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$needRemoveHint$0$DialogsActivity$10(this.arg$2, dialogInterface, i);
    }
}
