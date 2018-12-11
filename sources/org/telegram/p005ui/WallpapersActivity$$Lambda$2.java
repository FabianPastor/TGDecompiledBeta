package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.WallpapersActivity$$Lambda$2 */
final /* synthetic */ class WallpapersActivity$$Lambda$2 implements RequestDelegate {
    private final WallpapersActivity arg$1;

    WallpapersActivity$$Lambda$2(WallpapersActivity wallpapersActivity) {
        this.arg$1 = wallpapersActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadWallpapers$3$WallpapersActivity(tLObject, tL_error);
    }
}
