package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class WallpapersListActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ WallpapersListActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ WallpapersListActivity$$ExternalSyntheticLambda6(WallpapersListActivity wallpapersListActivity, boolean z) {
        this.f$0 = wallpapersListActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadWallpapers$6(this.f$1, tLObject, tLRPC$TL_error);
    }
}
