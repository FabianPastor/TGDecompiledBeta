package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChatActivity.C14815;

final /* synthetic */ class ChatActivity$5$$Lambda$0 implements OnClickListener {
    private final C14815 arg$1;
    private final int arg$2;
    private final boolean arg$3;

    ChatActivity$5$$Lambda$0(C14815 c14815, int i, boolean z) {
        this.arg$1 = c14815;
        this.arg$2 = i;
        this.arg$3 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ChatActivity$5(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
