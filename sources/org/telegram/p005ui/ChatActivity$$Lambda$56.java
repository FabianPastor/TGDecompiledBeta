package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$56 */
final /* synthetic */ class ChatActivity$$Lambda$56 implements OnClickListener {
    private final ChatActivity arg$1;
    private final int arg$2;
    private final boolean[] arg$3;

    ChatActivity$$Lambda$56(ChatActivity chatActivity, int i, boolean[] zArr) {
        this.arg$1 = chatActivity;
        this.arg$2 = i;
        this.arg$3 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processSelectedOption$72$ChatActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
