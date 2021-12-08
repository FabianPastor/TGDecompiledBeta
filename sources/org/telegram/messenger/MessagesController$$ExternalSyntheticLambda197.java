package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda197 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ MessagesController.SendAsPeersInfo f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda197(MessagesController messagesController, long j, MessagesController.SendAsPeersInfo sendAsPeersInfo) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = sendAsPeersInfo;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m227x37a735cc(this.f$1, this.f$2, tLObject, tL_error);
    }
}
