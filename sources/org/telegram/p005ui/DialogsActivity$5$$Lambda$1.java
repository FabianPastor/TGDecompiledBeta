package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.DialogsActivity.CLASSNAME;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_dialog;

/* renamed from: org.telegram.ui.DialogsActivity$5$$Lambda$1 */
final /* synthetic */ class DialogsActivity$5$$Lambda$1 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final boolean arg$2;
    private final boolean arg$3;
    private final TL_dialog arg$4;
    private final Chat arg$5;

    DialogsActivity$5$$Lambda$1(CLASSNAME CLASSNAME, boolean z, boolean z2, TL_dialog tL_dialog, Chat chat) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = z;
        this.arg$3 = z2;
        this.arg$4 = tL_dialog;
        this.arg$5 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$3$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
