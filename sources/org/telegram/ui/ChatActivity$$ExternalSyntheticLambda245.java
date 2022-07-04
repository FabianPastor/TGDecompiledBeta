package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda245 implements View.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.KeyboardButton f$1;
    public final /* synthetic */ MessageObject f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda245(ChatActivity chatActivity, TLRPC.KeyboardButton keyboardButton, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = keyboardButton;
        this.f$2 = messageObject;
    }

    public final void onClick(View view) {
        this.f$0.m3127lambda$updatePinnedMessageView$143$orgtelegramuiChatActivity(this.f$1, this.f$2, view);
    }
}
