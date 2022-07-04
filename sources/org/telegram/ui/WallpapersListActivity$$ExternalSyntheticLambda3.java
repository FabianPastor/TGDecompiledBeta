package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class WallpapersListActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ WallpapersListActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ WallpapersListActivity$$ExternalSyntheticLambda3(WallpapersListActivity wallpapersListActivity, TLObject tLObject, boolean z) {
        this.f$0 = wallpapersListActivity;
        this.f$1 = tLObject;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$loadWallpapers$5(this.f$1, this.f$2);
    }
}
