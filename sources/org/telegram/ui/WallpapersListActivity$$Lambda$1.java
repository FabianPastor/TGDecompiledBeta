package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class WallpapersListActivity$$Lambda$1 implements RequestDelegate {
    private final WallpapersListActivity arg$1;

    WallpapersListActivity$$Lambda$1(WallpapersListActivity wallpapersListActivity) {
        this.arg$1 = wallpapersListActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadWallpapers$2$WallpapersListActivity(tLObject, tL_error);
    }
}
