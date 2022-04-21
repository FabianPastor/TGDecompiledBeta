package org.telegram.messenger;

import java.util.List;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda216 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ List f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda216(MessagesController messagesController, long j, List list) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = list;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m388xd5084db8(this.f$1, this.f$2, tLObject, tL_error);
    }
}
