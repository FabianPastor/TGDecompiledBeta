package org.telegram.messenger;

import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$bGlnbD0Mf3yn2_2a_iqo-KIMiFo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ OverrideWallpaperInfo f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$bGlnbD0Mf3yn2_2a_iqo-KIMiFo(MessagesStorage messagesStorage, OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = overrideWallpaperInfo;
        this.f$2 = z;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$null$16$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
