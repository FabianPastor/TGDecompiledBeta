package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.DialogsActivity.C15825;

final /* synthetic */ class DialogsActivity$5$$Lambda$4 implements OnClickListener {
    private final C15825 arg$1;
    private final Chat arg$2;

    DialogsActivity$5$$Lambda$4(C15825 c15825, Chat chat) {
        this.arg$1 = c15825;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$DialogsActivity$5(this.arg$2, dialogInterface, i);
    }
}
