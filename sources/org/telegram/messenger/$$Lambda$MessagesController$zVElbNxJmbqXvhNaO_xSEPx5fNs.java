package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$zVElbNxJmbqXvhNaO_xSEPx5fNs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$zVElbNxJmbqXvhNaO_xSEPx5fNs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$zVElbNxJmbqXvhNaO_xSEPx5fNs INSTANCE = new $$Lambda$MessagesController$zVElbNxJmbqXvhNaO_xSEPx5fNs();

    private /* synthetic */ $$Lambda$MessagesController$zVElbNxJmbqXvhNaO_xSEPx5fNs() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$219(tLObject, tLRPC$TL_error);
    }
}
