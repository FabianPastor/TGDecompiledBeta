package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$iKqcy77skSBdjlZKwGcVs7bhl_g  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$iKqcy77skSBdjlZKwGcVs7bhl_g implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$iKqcy77skSBdjlZKwGcVs7bhl_g INSTANCE = new $$Lambda$MediaDataController$iKqcy77skSBdjlZKwGcVs7bhl_g();

    private /* synthetic */ $$Lambda$MediaDataController$iKqcy77skSBdjlZKwGcVs7bhl_g() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$97(tLObject, tLRPC$TL_error);
    }
}
