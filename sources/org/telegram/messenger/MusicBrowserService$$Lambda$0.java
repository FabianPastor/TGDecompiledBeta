package org.telegram.messenger;

import android.service.media.MediaBrowserService.Result;

final /* synthetic */ class MusicBrowserService$$Lambda$0 implements Runnable {
    private final MusicBrowserService arg$1;
    private final MessagesStorage arg$2;
    private final String arg$3;
    private final Result arg$4;

    MusicBrowserService$$Lambda$0(MusicBrowserService musicBrowserService, MessagesStorage messagesStorage, String str, Result result) {
        this.arg$1 = musicBrowserService;
        this.arg$2 = messagesStorage;
        this.arg$3 = str;
        this.arg$4 = result;
    }

    public void run() {
        this.arg$1.lambda$onLoadChildren$1$MusicBrowserService(this.arg$2, this.arg$3, this.arg$4);
    }
}
