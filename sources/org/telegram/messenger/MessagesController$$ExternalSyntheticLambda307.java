package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_setChatAvailableReactions;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda307 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$TL_messages_setChatAvailableReactions f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda307(MessagesController messagesController, long j, TLRPC$TL_messages_setChatAvailableReactions tLRPC$TL_messages_setChatAvailableReactions) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = tLRPC$TL_messages_setChatAvailableReactions;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setChatReactions$359(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
