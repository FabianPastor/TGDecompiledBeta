package org.telegram.ui.ActionBar;

import java.io.File;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ Theme.OverrideWallpaperInfo f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLRPC.Document f$5;

    public /* synthetic */ Theme$$ExternalSyntheticLambda7(Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file, int i, boolean z, boolean z2, TLRPC.Document document) {
        this.f$0 = overrideWallpaperInfo;
        this.f$1 = file;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = document;
    }

    public final void run() {
        Theme.lambda$loadWallpaper$8(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
