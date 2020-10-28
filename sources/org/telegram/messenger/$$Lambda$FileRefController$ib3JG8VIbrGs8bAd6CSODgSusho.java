package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$FileRefController$ib3JG8VIbrGs8bAd6CSODgSusho  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FileRefController$ib3JG8VIbrGs8bAd6CSODgSusho implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FileRefController$ib3JG8VIbrGs8bAd6CSODgSusho INSTANCE = new $$Lambda$FileRefController$ib3JG8VIbrGs8bAd6CSODgSusho();

    private /* synthetic */ $$Lambda$FileRefController$ib3JG8VIbrGs8bAd6CSODgSusho() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$25(tLObject, tLRPC$TL_error);
    }
}
