package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$67 implements OnClickListener {
    private final ChatActivity arg$1;
    private final int arg$2;

    ChatActivity$$Lambda$67(ChatActivity chatActivity, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$60$ChatActivity(this.arg$2, dialogInterface, i);
    }
}
