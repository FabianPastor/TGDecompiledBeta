package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$D1wmTTdNdz1-PHCLASSNAMEjxVWGiXXNA implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;

    public /* synthetic */ -$$Lambda$MediaDataController$D1wmTTdNdz1-PHCLASSNAMEjxVWGiXXNA(MediaDataController mediaDataController) {
        this.f$0 = mediaDataController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadGroupStickerSet$10$MediaDataController(tLObject, tL_error);
    }
}
