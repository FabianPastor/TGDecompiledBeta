package org.telegram.ui;

import org.telegram.ui.WallpaperActivity.WallpaperActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$tPCre3L2K_38M9O_G5mv57D0Uc4 implements WallpaperActivityDelegate {
    private final /* synthetic */ WallpapersListActivity f$0;

    public /* synthetic */ -$$Lambda$tPCre3L2K_38M9O_G5mv57D0Uc4(WallpapersListActivity wallpapersListActivity) {
        this.f$0 = wallpapersListActivity;
    }

    public final void didSetNewBackground() {
        this.f$0.removeSelfFromStack();
    }
}
