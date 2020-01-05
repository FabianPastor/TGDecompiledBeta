package org.telegram.ui.ActionBar;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$2f8NbctloYmhmpPWZuMMuEu_eSE implements Runnable {
    private final /* synthetic */ OverrideWallpaperInfo f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ File f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$Theme$2f8NbctloYmhmpPWZuMMuEu_eSE(OverrideWallpaperInfo overrideWallpaperInfo, boolean z, File file, boolean z2) {
        this.f$0 = overrideWallpaperInfo;
        this.f$1 = z;
        this.f$2 = file;
        this.f$3 = z2;
    }

    public final void run() {
        Theme.lambda$loadWallpaper$8(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
