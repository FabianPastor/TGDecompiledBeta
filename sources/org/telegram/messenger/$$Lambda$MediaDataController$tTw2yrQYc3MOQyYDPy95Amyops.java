package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$tTw2yrQYc3MOQyYDPy95Amyo-ps  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$tTw2yrQYc3MOQyYDPy95Amyops implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$tTw2yrQYc3MOQyYDPy95Amyops INSTANCE = new $$Lambda$MediaDataController$tTw2yrQYc3MOQyYDPy95Amyops();

    private /* synthetic */ $$Lambda$MediaDataController$tTw2yrQYc3MOQyYDPy95Amyops() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$85(tLObject, tLRPC$TL_error);
    }
}
