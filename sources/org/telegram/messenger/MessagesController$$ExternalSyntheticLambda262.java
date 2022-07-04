package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda262 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda262(MessagesController messagesController, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m433x1718dcc7(this.f$1, tLObject, tL_error);
    }
}
