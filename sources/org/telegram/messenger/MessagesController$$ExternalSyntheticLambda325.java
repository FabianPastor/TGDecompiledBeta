package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda325 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda325(MessagesController messagesController, TLRPC$ChatFull tLRPC$ChatFull, String str) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$ChatFull;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$updateChatAbout$238(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
