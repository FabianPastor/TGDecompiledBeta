package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ArchivedStickersActivity$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ ArchivedStickersActivity f$0;

    public /* synthetic */ ArchivedStickersActivity$$ExternalSyntheticLambda2(ArchivedStickersActivity archivedStickersActivity) {
        this.f$0 = archivedStickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getStickers$2(tLObject, tLRPC$TL_error);
    }
}
