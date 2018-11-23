package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ChatActivity.C13135;

/* renamed from: org.telegram.ui.ChatActivity$5$$Lambda$0 */
final /* synthetic */ class ChatActivity$5$$Lambda$0 implements OnClickListener {
    private final C13135 arg$1;
    private final int arg$2;
    private final boolean arg$3;

    ChatActivity$5$$Lambda$0(C13135 c13135, int i, boolean z) {
        this.arg$1 = c13135;
        this.arg$2 = i;
        this.arg$3 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$ChatActivity$5(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
