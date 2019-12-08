package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_getBlocked;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$P7nV03ZbMlhqhHwEi6tP_PYFeCs implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TL_contacts_getBlocked f$3;

    public /* synthetic */ -$$Lambda$MessagesController$P7nV03ZbMlhqhHwEi6tP_PYFeCs(MessagesController messagesController, TLObject tLObject, boolean z, TL_contacts_getBlocked tL_contacts_getBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
        this.f$2 = z;
        this.f$3 = tL_contacts_getBlocked;
    }

    public final void run() {
        this.f$0.lambda$null$64$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
