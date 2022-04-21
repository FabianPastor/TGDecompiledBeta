package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda182 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.Chat f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda182(MessagesController messagesController, int i, TLRPC.Chat chat, TLRPC.User user, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = chat;
        this.f$3 = user;
        this.f$4 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m309lambda$pinMessage$102$orgtelegrammessengerMessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
