package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda118 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ Runnable f$5;
    public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$6;
    public final /* synthetic */ TLRPC$Chat f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ MessageObject f$9;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda118(ChatActivity chatActivity, int i, TLObject tLObject, int i2, MessageObject messageObject, Runnable runnable, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, TLRPC$Chat tLRPC$Chat, int i3, MessageObject messageObject2) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = i2;
        this.f$4 = messageObject;
        this.f$5 = runnable;
        this.f$6 = tLRPC$TL_messages_getDiscussionMessage;
        this.f$7 = tLRPC$Chat;
        this.f$8 = i3;
        this.f$9 = messageObject2;
    }

    public final void run() {
        this.f$0.lambda$openDiscussionMessageChat$173(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
