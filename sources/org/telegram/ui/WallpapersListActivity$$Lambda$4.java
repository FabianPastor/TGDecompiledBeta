package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class WallpapersListActivity$$Lambda$4 implements Runnable {
    private final WallpapersListActivity arg$1;
    private final int arg$2;
    private final TLObject arg$3;

    WallpapersListActivity$$Lambda$4(WallpapersListActivity wallpapersListActivity, int i, TLObject tLObject) {
        this.arg$1 = wallpapersListActivity;
        this.arg$2 = i;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$5$WallpapersListActivity(this.arg$2, this.arg$3);
    }
}
