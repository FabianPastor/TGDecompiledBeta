package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$dzpxMpGl-n07NLcFQNls0m1zW-U  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$dzpxMpGln07NLcFQNls0m1zWU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$dzpxMpGln07NLcFQNls0m1zWU INSTANCE = new $$Lambda$MediaDataController$dzpxMpGln07NLcFQNls0m1zWU();

    private /* synthetic */ $$Lambda$MediaDataController$dzpxMpGln07NLcFQNls0m1zWU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$84(tLObject, tLRPC$TL_error);
    }
}
