package org.telegram.ui.ActionBar;

import android.graphics.Bitmap;
import java.io.File;

public final /* synthetic */ class EmojiThemes$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ File f$0;
    public final /* synthetic */ Bitmap f$1;

    public /* synthetic */ EmojiThemes$$ExternalSyntheticLambda0(File file, Bitmap bitmap) {
        this.f$0 = file;
        this.f$1 = bitmap;
    }

    public final void run() {
        EmojiThemes.lambda$loadWallpaperThumb$2(this.f$0, this.f$1);
    }
}
