package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.DialogsActivity.C13365;

final /* synthetic */ class DialogsActivity$5$$Lambda$4 implements OnClickListener {
    private final C13365 arg$1;
    private final Chat arg$2;

    DialogsActivity$5$$Lambda$4(C13365 c13365, Chat chat) {
        this.arg$1 = c13365;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$DialogsActivity$5(this.arg$2, dialogInterface, i);
    }
}
