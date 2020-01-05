package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Ff_w1WJnzH_NpUM3KwOnMO3hJe8 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ OverrideWallpaperInfo f$1;
    private final /* synthetic */ TL_wallPaperSettings f$2;

    public /* synthetic */ -$$Lambda$MessagesController$Ff_w1WJnzH_NpUM3KwOnMO3hJe8(MessagesController messagesController, OverrideWallpaperInfo overrideWallpaperInfo, TL_wallPaperSettings tL_wallPaperSettings) {
        this.f$0 = messagesController;
        this.f$1 = overrideWallpaperInfo;
        this.f$2 = tL_wallPaperSettings;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$10$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
