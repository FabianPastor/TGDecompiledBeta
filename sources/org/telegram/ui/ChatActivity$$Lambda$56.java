package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$56 implements OnClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$56(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$processSelectedOption$72$ChatActivity(dialogInterface, i);
    }
}
