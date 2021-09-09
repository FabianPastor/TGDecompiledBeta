package org.telegram.ui.ActionBar;

import java.io.File;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ Theme.OverrideWallpaperInfo f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ Theme$$ExternalSyntheticLambda5(Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file, int i, boolean z, boolean z2) {
        this.f$0 = overrideWallpaperInfo;
        this.f$1 = file;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = z2;
    }

    public final void run() {
        Theme.lambda$loadWallpaper$8(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
