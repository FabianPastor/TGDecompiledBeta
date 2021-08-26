package org.telegram.messenger;

import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda168 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ Theme.OverrideWallpaperInfo f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda168(MessagesStorage messagesStorage, Theme.OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = overrideWallpaperInfo;
        this.f$2 = z;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$16(this.f$1, this.f$2, this.f$3);
    }
}
