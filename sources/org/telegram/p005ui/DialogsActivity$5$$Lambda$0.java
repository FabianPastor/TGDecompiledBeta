package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.DialogsActivity.CLASSNAME;

/* renamed from: org.telegram.ui.DialogsActivity$5$$Lambda$0 */
final /* synthetic */ class DialogsActivity$5$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;

    DialogsActivity$5$$Lambda$0(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$DialogsActivity$5(dialogInterface, i);
    }
}
