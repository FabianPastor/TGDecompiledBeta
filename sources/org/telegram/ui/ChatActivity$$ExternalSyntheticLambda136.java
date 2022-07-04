package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda136 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ TLRPC.TL_messages_getDiscussionMessage f$4;
    public final /* synthetic */ TLRPC.Chat f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ MessageObject f$7;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda136(ChatActivity chatActivity, int i, int i2, MessageObject messageObject, TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage, TLRPC.Chat chat, int i3, MessageObject messageObject2) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = messageObject;
        this.f$4 = tL_messages_getDiscussionMessage;
        this.f$5 = chat;
        this.f$6 = i3;
        this.f$7 = messageObject2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3046x4b328dc0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
    }
}
