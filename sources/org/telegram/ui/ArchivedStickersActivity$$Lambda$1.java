package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ArchivedStickersActivity$$Lambda$1 implements RequestDelegate {
    private final ArchivedStickersActivity arg$1;

    ArchivedStickersActivity$$Lambda$1(ArchivedStickersActivity archivedStickersActivity) {
        this.arg$1 = archivedStickersActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getStickers$2$ArchivedStickersActivity(tLObject, tL_error);
    }
}
