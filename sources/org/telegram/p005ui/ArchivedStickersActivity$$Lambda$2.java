package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ArchivedStickersActivity$$Lambda$2 */
final /* synthetic */ class ArchivedStickersActivity$$Lambda$2 implements Runnable {
    private final ArchivedStickersActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    ArchivedStickersActivity$$Lambda$2(ArchivedStickersActivity archivedStickersActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = archivedStickersActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$1$ArchivedStickersActivity(this.arg$2, this.arg$3);
    }
}
