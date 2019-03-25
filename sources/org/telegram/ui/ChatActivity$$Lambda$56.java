package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

final /* synthetic */ class ChatActivity$$Lambda$56 implements OnClickListener {
    private final ChatActivity arg$1;
    private final MessageObject arg$2;

    ChatActivity$$Lambda$56(ChatActivity chatActivity, MessageObject messageObject) {
        this.arg$1 = chatActivity;
        this.arg$2 = messageObject;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processSelectedOption$74$ChatActivity(this.arg$2, dialogInterface, i);
    }
}
