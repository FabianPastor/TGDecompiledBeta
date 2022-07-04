package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda259 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Theme.OverrideWallpaperInfo f$1;
    public final /* synthetic */ TLRPC.TL_wallPaperSettings f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda259(MessagesController messagesController, Theme.OverrideWallpaperInfo overrideWallpaperInfo, TLRPC.TL_wallPaperSettings tL_wallPaperSettings) {
        this.f$0 = messagesController;
        this.f$1 = overrideWallpaperInfo;
        this.f$2 = tL_wallPaperSettings;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m197xb04732dc(this.f$1, this.f$2, tLObject, tL_error);
    }
}
