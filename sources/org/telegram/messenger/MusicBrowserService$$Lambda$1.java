package org.telegram.messenger;

import android.service.media.MediaBrowserService.Result;

final /* synthetic */ class MusicBrowserService$$Lambda$1 implements Runnable {
    private final MusicBrowserService arg$1;
    private final String arg$2;
    private final Result arg$3;

    MusicBrowserService$$Lambda$1(MusicBrowserService musicBrowserService, String str, Result result) {
        this.arg$1 = musicBrowserService;
        this.arg$2 = str;
        this.arg$3 = result;
    }

    public void run() {
        this.arg$1.lambda$null$0$MusicBrowserService(this.arg$2, this.arg$3);
    }
}
