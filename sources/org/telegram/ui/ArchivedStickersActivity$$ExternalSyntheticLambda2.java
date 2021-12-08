package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ArchivedStickersActivity$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ ArchivedStickersActivity f$0;

    public /* synthetic */ ArchivedStickersActivity$$ExternalSyntheticLambda2(ArchivedStickersActivity archivedStickersActivity) {
        this.f$0 = archivedStickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1408lambda$getStickers$2$orgtelegramuiArchivedStickersActivity(tLObject, tL_error);
    }
}
