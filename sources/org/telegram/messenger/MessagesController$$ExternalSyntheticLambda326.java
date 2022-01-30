package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda326 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$TL_contacts_getBlocked f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda326(MessagesController messagesController, boolean z, TLRPC$TL_contacts_getBlocked tLRPC$TL_contacts_getBlocked) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tLRPC$TL_contacts_getBlocked;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getBlockedPeers$85(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
