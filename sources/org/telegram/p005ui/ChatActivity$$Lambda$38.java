package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$38 */
final /* synthetic */ class ChatActivity$$Lambda$38 implements OnClickListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$38(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onRequestPermissionsResultFragment$49$ChatActivity(dialogInterface, i);
    }
}
