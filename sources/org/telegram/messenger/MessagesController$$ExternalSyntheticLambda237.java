package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda237 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC.TL_messages_saveRecentSticker f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda237(MessagesController messagesController, Object obj, TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker) {
        this.f$0 = messagesController;
        this.f$1 = obj;
        this.f$2 = tL_messages_saveRecentSticker;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m379xce84869c(this.f$1, this.f$2, tLObject, tL_error);
    }
}
