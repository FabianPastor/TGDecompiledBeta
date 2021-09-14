package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda290 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$InputPeer f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda290(MessagesController messagesController, TLRPC$InputPeer tLRPC$InputPeer, long j) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$InputPeer;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$reloadMentionsCountForChannel$170(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
