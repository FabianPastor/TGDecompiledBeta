package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getDiscussionMessage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda102 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ MessageObject f$11;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC$TL_error f$4;
    public final /* synthetic */ Runnable f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ TLRPC$TL_messages_getDiscussionMessage f$8;
    public final /* synthetic */ TLRPC$Chat f$9;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda102(ChatActivity chatActivity, int i, TLObject tLObject, int i2, TLRPC$TL_error tLRPC$TL_error, Runnable runnable, int i3, MessageObject messageObject, TLRPC$TL_messages_getDiscussionMessage tLRPC$TL_messages_getDiscussionMessage, TLRPC$Chat tLRPC$Chat, int i4, MessageObject messageObject2) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = i2;
        this.f$4 = tLRPC$TL_error;
        this.f$5 = runnable;
        this.f$6 = i3;
        this.f$7 = messageObject;
        this.f$8 = tLRPC$TL_messages_getDiscussionMessage;
        this.f$9 = tLRPC$Chat;
        this.f$10 = i4;
        this.f$11 = messageObject2;
    }

    public final void run() {
        this.f$0.lambda$openDiscussionMessageChat$143(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
    }
}
