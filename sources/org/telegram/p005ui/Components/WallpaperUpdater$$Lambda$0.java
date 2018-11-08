package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.WallpaperUpdater$$Lambda$0 */
final /* synthetic */ class WallpaperUpdater$$Lambda$0 implements OnClickListener {
    private final WallpaperUpdater arg$1;
    private final boolean arg$2;

    WallpaperUpdater$$Lambda$0(WallpaperUpdater wallpaperUpdater, boolean z) {
        this.arg$1 = wallpaperUpdater;
        this.arg$2 = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showAlert$0$WallpaperUpdater(this.arg$2, dialogInterface, i);
    }
}
