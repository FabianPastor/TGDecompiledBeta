package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

final /* synthetic */ class ChatActivity$$Lambda$29 implements OnClickListener {
    private final ChatActivity arg$1;
    private final MessageObject arg$2;

    ChatActivity$$Lambda$29(ChatActivity chatActivity, MessageObject messageObject) {
        this.arg$1 = chatActivity;
        this.arg$2 = messageObject;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$shareMyContact$37$ChatActivity(this.arg$2, dialogInterface, i);
    }
}
