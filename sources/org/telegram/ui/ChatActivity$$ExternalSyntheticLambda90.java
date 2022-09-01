package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$KeyboardButton;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda90 implements View.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$KeyboardButton f$1;
    public final /* synthetic */ MessageObject f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda90(ChatActivity chatActivity, TLRPC$KeyboardButton tLRPC$KeyboardButton, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$KeyboardButton;
        this.f$2 = messageObject;
    }

    public final void onClick(View view) {
        this.f$0.lambda$updatePinnedMessageView$143(this.f$1, this.f$2, view);
    }
}
