package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class WallpapersListActivity$$Lambda$3 implements RequestDelegate {
    private final WallpapersListActivity arg$1;
    private final int arg$2;

    WallpapersListActivity$$Lambda$3(WallpapersListActivity wallpapersListActivity, int i) {
        this.arg$1 = wallpapersListActivity;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchImages$6$WallpapersListActivity(this.arg$2, tLObject, tL_error);
    }
}
