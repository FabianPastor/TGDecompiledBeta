package org.telegram.ui;

import org.telegram.ui.ThemePreviewActivity.WallpaperActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$obLeTjuOecC5VgqnkRKKKiZf1rM implements WallpaperActivityDelegate {
    private final /* synthetic */ WallpapersListActivity f$0;

    public /* synthetic */ -$$Lambda$obLeTjuOecC5VgqnkRKKKiZf1rM(WallpapersListActivity wallpapersListActivity) {
        this.f$0 = wallpapersListActivity;
    }

    public final void didSetNewBackground() {
        this.f$0.removeSelfFromStack();
    }
}
