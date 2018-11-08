package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ChatActivity.C11545;

/* renamed from: org.telegram.ui.ChatActivity$5$$Lambda$0 */
final /* synthetic */ class ChatActivity$5$$Lambda$0 implements OnClickListener {
    private final C11545 arg$1;
    private final int arg$2;
    private final boolean arg$3;

    ChatActivity$5$$Lambda$0(C11545 c11545, int i, boolean z) {
        this.arg$1 = c11545;
        this.arg$2 = i;
        this.arg$3 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ChatActivity$5(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
