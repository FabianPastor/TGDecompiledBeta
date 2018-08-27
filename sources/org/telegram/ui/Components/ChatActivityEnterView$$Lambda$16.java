package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;

final /* synthetic */ class ChatActivityEnterView$$Lambda$16 implements OnClickListener {
    private final ChatActivityEnterView arg$1;
    private final MessageObject arg$2;
    private final KeyboardButton arg$3;

    ChatActivityEnterView$$Lambda$16(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, KeyboardButton keyboardButton) {
        this.arg$1 = chatActivityEnterView;
        this.arg$2 = messageObject;
        this.arg$3 = keyboardButton;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didPressedBotButton$16$ChatActivityEnterView(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
