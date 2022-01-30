package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda321 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Theme.OverrideWallpaperInfo f$1;
    public final /* synthetic */ TLRPC$TL_wallPaperSettings f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda321(MessagesController messagesController, Theme.OverrideWallpaperInfo overrideWallpaperInfo, TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings) {
        this.f$0 = messagesController;
        this.f$1 = overrideWallpaperInfo;
        this.f$2 = tLRPC$TL_wallPaperSettings;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didReceivedNotification$26(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
