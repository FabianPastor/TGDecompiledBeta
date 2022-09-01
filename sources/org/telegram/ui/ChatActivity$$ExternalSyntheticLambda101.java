package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda101 implements View.OnLongClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$KeyboardButton f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ ChatActivity.PinnedMessageButton f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda101(ChatActivity chatActivity, TLRPC$KeyboardButton tLRPC$KeyboardButton, MessageObject messageObject, ChatActivity.PinnedMessageButton pinnedMessageButton) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$KeyboardButton;
        this.f$2 = messageObject;
        this.f$3 = pinnedMessageButton;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.lambda$updatePinnedMessageView$144(this.f$1, this.f$2, this.f$3, view);
    }
}
