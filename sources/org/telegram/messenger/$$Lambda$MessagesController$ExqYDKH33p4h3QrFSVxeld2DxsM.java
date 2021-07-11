package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ExqYDKH33p4h3QrFSVxeld2DxsM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ExqYDKH33p4h3QrFSVxeld2DxsM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ExqYDKH33p4h3QrFSVxeld2DxsM INSTANCE = new $$Lambda$MessagesController$ExqYDKH33p4h3QrFSVxeld2DxsM();

    private /* synthetic */ $$Lambda$MessagesController$ExqYDKH33p4h3QrFSVxeld2DxsM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$85(tLObject, tLRPC$TL_error);
    }
}
