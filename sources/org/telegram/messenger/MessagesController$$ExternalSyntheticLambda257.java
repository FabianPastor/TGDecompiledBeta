package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda257 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda257(MessagesController messagesController, boolean z, TLRPC.User user, long j) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = user;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m185xvar_(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
