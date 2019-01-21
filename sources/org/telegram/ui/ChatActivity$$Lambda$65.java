package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$65 implements OnClickListener {
    private final ChatActivity arg$1;
    private final String arg$2;

    ChatActivity$$Lambda$65(ChatActivity chatActivity, String str) {
        this.arg$1 = chatActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showOpenUrlAlert$85$ChatActivity(this.arg$2, dialogInterface, i);
    }
}
