package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda241 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.InputPeer f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda241(MessagesController messagesController, TLRPC.InputPeer inputPeer, long j) {
        this.f$0 = messagesController;
        this.f$1 = inputPeer;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m364x70a37878(this.f$1, this.f$2, tLObject, tL_error);
    }
}
