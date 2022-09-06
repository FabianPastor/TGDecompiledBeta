package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_contacts_getBlocked;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda146 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$TL_contacts_getBlocked f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda146(MessagesController messagesController, TLObject tLObject, boolean z, TLRPC$TL_contacts_getBlocked tLRPC$TL_contacts_getBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
        this.f$2 = z;
        this.f$3 = tLRPC$TL_contacts_getBlocked;
    }

    public final void run() {
        this.f$0.lambda$getBlockedPeers$89(this.f$1, this.f$2, this.f$3);
    }
}
