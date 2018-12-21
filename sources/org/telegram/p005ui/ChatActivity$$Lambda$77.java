package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$77 */
final /* synthetic */ class ChatActivity$$Lambda$77 implements OnCancelListener {
    private final ChatActivity arg$1;
    private final int arg$2;

    ChatActivity$$Lambda$77(ChatActivity chatActivity, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$null$61$ChatActivity(this.arg$2, dialogInterface);
    }
}
