package org.telegram.messenger;

import android.service.media.MediaBrowserService.Result;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MusicBrowserService$iS7bPWX5pXtbCNrWxnzS-j3JBYQ implements Runnable {
    private final /* synthetic */ MusicBrowserService f$0;
    private final /* synthetic */ MessagesStorage f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Result f$3;

    public /* synthetic */ -$$Lambda$MusicBrowserService$iS7bPWX5pXtbCNrWxnzS-j3JBYQ(MusicBrowserService musicBrowserService, MessagesStorage messagesStorage, String str, Result result) {
        this.f$0 = musicBrowserService;
        this.f$1 = messagesStorage;
        this.f$2 = str;
        this.f$3 = result;
    }

    public final void run() {
        this.f$0.lambda$onLoadChildren$1$MusicBrowserService(this.f$1, this.f$2, this.f$3);
    }
}
