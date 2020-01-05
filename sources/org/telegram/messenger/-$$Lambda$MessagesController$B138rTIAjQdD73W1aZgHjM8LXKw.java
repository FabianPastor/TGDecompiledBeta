package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$B138rTIAjQdD73W1aZgHjM8LXKw implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_wallPaper f$1;
    private final /* synthetic */ TL_wallPaperSettings f$2;
    private final /* synthetic */ OverrideWallpaperInfo f$3;
    private final /* synthetic */ File f$4;

    public /* synthetic */ -$$Lambda$MessagesController$B138rTIAjQdD73W1aZgHjM8LXKw(MessagesController messagesController, TL_wallPaper tL_wallPaper, TL_wallPaperSettings tL_wallPaperSettings, OverrideWallpaperInfo overrideWallpaperInfo, File file) {
        this.f$0 = messagesController;
        this.f$1 = tL_wallPaper;
        this.f$2 = tL_wallPaperSettings;
        this.f$3 = overrideWallpaperInfo;
        this.f$4 = file;
    }

    public final void run() {
        this.f$0.lambda$null$9$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
