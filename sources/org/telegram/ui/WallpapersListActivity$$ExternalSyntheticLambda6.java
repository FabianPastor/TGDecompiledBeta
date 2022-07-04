package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class WallpapersListActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ WallpapersListActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ WallpapersListActivity$$ExternalSyntheticLambda6(WallpapersListActivity wallpapersListActivity, boolean z) {
        this.f$0 = wallpapersListActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4843lambda$loadWallpapers$6$orgtelegramuiWallpapersListActivity(this.f$1, tLObject, tL_error);
    }
}
