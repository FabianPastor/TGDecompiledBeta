package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$18 implements OnClickListener {
    private final ChatEditTypeActivity arg$1;
    private final Chat arg$2;

    ChatEditTypeActivity$$Lambda$18(ChatEditTypeActivity chatEditTypeActivity, Chat chat) {
        this.arg$1 = chatEditTypeActivity;
        this.arg$2 = chat;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$13$ChatEditTypeActivity(this.arg$2, dialogInterface, i);
    }
}
