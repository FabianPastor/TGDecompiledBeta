package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$KNuUiyiitxgZvcXPZbDv0woOl9I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$KNuUiyiitxgZvcXPZbDv0woOl9I implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$KNuUiyiitxgZvcXPZbDv0woOl9I INSTANCE = new $$Lambda$MessagesController$KNuUiyiitxgZvcXPZbDv0woOl9I();

    private /* synthetic */ $$Lambda$MessagesController$KNuUiyiitxgZvcXPZbDv0woOl9I() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$77(tLObject, tLRPC$TL_error);
    }
}
