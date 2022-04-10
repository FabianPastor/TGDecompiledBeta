package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$KeyboardButton;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$KeyboardButton f$3;
    public final /* synthetic */ MessageObject f$4;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda43(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, long j, TLRPC$KeyboardButton tLRPC$KeyboardButton, MessageObject messageObject2) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = messageObject;
        this.f$2 = j;
        this.f$3 = tLRPC$KeyboardButton;
        this.f$4 = messageObject2;
    }

    public final void run() {
        this.f$0.lambda$didPressedBotButton$43(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
