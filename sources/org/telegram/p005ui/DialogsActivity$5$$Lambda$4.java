package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.DialogsActivity.CLASSNAME;
import org.telegram.tgnet.TLRPC.Chat;

/* renamed from: org.telegram.ui.DialogsActivity$5$$Lambda$4 */
final /* synthetic */ class DialogsActivity$5$$Lambda$4 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final Chat arg$2;

    DialogsActivity$5$$Lambda$4(CLASSNAME CLASSNAME, Chat chat) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$DialogsActivity$5(this.arg$2, dialogInterface, i);
    }
}
