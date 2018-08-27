package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.TL_game;

final /* synthetic */ class ChatActivity$$Lambda$60 implements OnClickListener {
    private final ChatActivity arg$1;
    private final TL_game arg$2;
    private final MessageObject arg$3;
    private final String arg$4;
    private final int arg$5;

    ChatActivity$$Lambda$60(ChatActivity chatActivity, TL_game tL_game, MessageObject messageObject, String str, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = tL_game;
        this.arg$3 = messageObject;
        this.arg$4 = str;
        this.arg$5 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showOpenGameAlert$76$ChatActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
