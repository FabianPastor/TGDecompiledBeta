package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda180 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$WallPaper f$1;
    public final /* synthetic */ TLRPC$TL_wallPaperSettings f$2;
    public final /* synthetic */ Theme.OverrideWallpaperInfo f$3;
    public final /* synthetic */ File f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda180(MessagesController messagesController, TLRPC$WallPaper tLRPC$WallPaper, TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings, Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$WallPaper;
        this.f$2 = tLRPC$TL_wallPaperSettings;
        this.f$3 = overrideWallpaperInfo;
        this.f$4 = file;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$25(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
