package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class WallpapersListActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ WallpapersListActivity f$0;

    public /* synthetic */ WallpapersListActivity$$ExternalSyntheticLambda5(WallpapersListActivity wallpapersListActivity) {
        this.f$0 = wallpapersListActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4076lambda$createView$2$orgtelegramuiWallpapersListActivity(tLObject, tL_error);
    }
}
