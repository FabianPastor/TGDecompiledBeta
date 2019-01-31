package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.WallpapersListActivity.AnonymousClass2;

final /* synthetic */ class WallpapersListActivity$2$$Lambda$2 implements RequestDelegate {
    private final AnonymousClass2 arg$1;
    private final int[] arg$2;

    WallpapersListActivity$2$$Lambda$2(AnonymousClass2 anonymousClass2, int[] iArr) {
        this.arg$1 = anonymousClass2;
        this.arg$2 = iArr;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$WallpapersListActivity$2(this.arg$2, tLObject, tL_error);
    }
}
