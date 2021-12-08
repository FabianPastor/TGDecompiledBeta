package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda201 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_messages_getMessagesViews f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda201(MessagesController messagesController, long j, TLRPC.TL_messages_getMessagesViews tL_messages_getMessagesViews) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = tL_messages_getMessagesViews;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m413x6eeb04a7(this.f$1, this.f$2, tLObject, tL_error);
    }
}
