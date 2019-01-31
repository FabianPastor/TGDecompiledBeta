package org.telegram.ui;

import java.util.Comparator;

final /* synthetic */ class WallpapersListActivity$$Lambda$4 implements Comparator {
    private final WallpapersListActivity arg$1;
    private final boolean arg$2;

    WallpapersListActivity$$Lambda$4(WallpapersListActivity wallpapersListActivity, boolean z) {
        this.arg$1 = wallpapersListActivity;
        this.arg$2 = z;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$fillWallpapersWithCustom$6$WallpapersListActivity(this.arg$2, obj, obj2);
    }
}
