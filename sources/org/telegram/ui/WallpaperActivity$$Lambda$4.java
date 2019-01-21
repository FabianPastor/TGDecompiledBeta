package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class WallpaperActivity$$Lambda$4 implements RequestDelegate {
    static final RequestDelegate $instance = new WallpaperActivity$$Lambda$4();

    private WallpaperActivity$$Lambda$4() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        WallpaperActivity.lambda$null$1$WallpaperActivity(tLObject, tL_error);
    }
}
