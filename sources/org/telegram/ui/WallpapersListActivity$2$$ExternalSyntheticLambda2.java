package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.WallpapersListActivity;

public final /* synthetic */ class WallpapersListActivity$2$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ WallpapersListActivity.AnonymousClass2 f$0;
    public final /* synthetic */ int[] f$1;

    public /* synthetic */ WallpapersListActivity$2$$ExternalSyntheticLambda2(WallpapersListActivity.AnonymousClass2 r1, int[] iArr) {
        this.f$0 = r1;
        this.f$1 = iArr;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3480lambda$onItemClick$1$orgtelegramuiWallpapersListActivity$2(this.f$1, tLObject, tL_error);
    }
}
