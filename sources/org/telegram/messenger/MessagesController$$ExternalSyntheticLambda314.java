package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda314 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC$TL_messages_saveGif f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda314(MessagesController messagesController, Object obj, TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif) {
        this.f$0 = messagesController;
        this.f$1 = obj;
        this.f$2 = tLRPC$TL_messages_saveGif;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$saveGif$116(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
