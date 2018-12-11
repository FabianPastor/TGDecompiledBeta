package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.WallpapersActivity$$Lambda$3 */
final /* synthetic */ class WallpapersActivity$$Lambda$3 implements Runnable {
    private final WallpapersActivity arg$1;
    private final TLObject arg$2;

    WallpapersActivity$$Lambda$3(WallpapersActivity wallpapersActivity, TLObject tLObject) {
        this.arg$1 = wallpapersActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$2$WallpapersActivity(this.arg$2);
    }
}
