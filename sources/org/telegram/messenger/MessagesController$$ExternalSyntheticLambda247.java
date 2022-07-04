package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda247 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda247(MessagesController messagesController, TLRPC.Chat chat, boolean z, long j) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = z;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m137x7a106617(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
