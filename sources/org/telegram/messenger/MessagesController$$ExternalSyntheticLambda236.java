package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda236 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda236(MessagesController messagesController, TLRPC.Chat chat, TLRPC.User user) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = user;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m413x5e7e1d3d(this.f$1, this.f$2, tLObject, tL_error);
    }
}
