package org.telegram.messenger;

import android.service.media.MediaBrowserService.Result;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MusicBrowserService$kPFLpCLASSNAMEuxOMtsW9zhDqaIzSat8 implements Runnable {
    private final /* synthetic */ MusicBrowserService f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ Result f$2;

    public /* synthetic */ -$$Lambda$MusicBrowserService$kPFLpCLASSNAMEuxOMtsW9zhDqaIzSat8(MusicBrowserService musicBrowserService, String str, Result result) {
        this.f$0 = musicBrowserService;
        this.f$1 = str;
        this.f$2 = result;
    }

    public final void run() {
        this.f$0.lambda$null$0$MusicBrowserService(this.f$1, this.f$2);
    }
}
