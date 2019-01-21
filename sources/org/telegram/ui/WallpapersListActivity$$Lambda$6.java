package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class WallpapersListActivity$$Lambda$6 implements Runnable {
    private final WallpapersListActivity arg$1;
    private final TLObject arg$2;

    WallpapersListActivity$$Lambda$6(WallpapersListActivity wallpapersListActivity, TLObject tLObject) {
        this.arg$1 = wallpapersListActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$1$WallpapersListActivity(this.arg$2);
    }
}
