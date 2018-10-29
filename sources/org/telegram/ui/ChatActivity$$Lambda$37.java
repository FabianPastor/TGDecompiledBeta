package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$37 implements OnClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$37(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onRequestPermissionsResultFragment$48$ChatActivity(dialogInterface, i);
    }
}
