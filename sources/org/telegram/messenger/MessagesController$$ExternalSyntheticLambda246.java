package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda246 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ TLRPC.Chat f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda246(MessagesController messagesController, TLRPC.Chat chat, TLRPC.User user, TLRPC.Chat chat2) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = user;
        this.f$3 = chat2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m189xbd060338(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
