package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

final /* synthetic */ class ChatActivityEnterView$$Lambda$17 implements DialogsActivityDelegate {
    private final ChatActivityEnterView arg$1;
    private final MessageObject arg$2;
    private final KeyboardButton arg$3;

    ChatActivityEnterView$$Lambda$17(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, KeyboardButton keyboardButton) {
        this.arg$1 = chatActivityEnterView;
        this.arg$2 = messageObject;
        this.arg$3 = keyboardButton;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$didPressedBotButton$17$ChatActivityEnterView(this.arg$2, this.arg$3, dialogsActivity, arrayList, charSequence, z);
    }
}
