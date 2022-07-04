package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda71 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ Long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda71(MessagesController messagesController, TLRPC.TL_error tL_error, TLObject tLObject, Long l) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = l;
    }

    public final void run() {
        this.f$0.m245xCLASSNAMEeb5(this.f$1, this.f$2, this.f$3);
    }
}
