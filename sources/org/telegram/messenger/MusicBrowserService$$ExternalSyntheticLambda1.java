package org.telegram.messenger;

import android.service.media.MediaBrowserService;

public final /* synthetic */ class MusicBrowserService$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ MusicBrowserService f$0;
    public final /* synthetic */ MessagesStorage f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ MediaBrowserService.Result f$3;

    public /* synthetic */ MusicBrowserService$$ExternalSyntheticLambda1(MusicBrowserService musicBrowserService, MessagesStorage messagesStorage, String str, MediaBrowserService.Result result) {
        this.f$0 = musicBrowserService;
        this.f$1 = messagesStorage;
        this.f$2 = str;
        this.f$3 = result;
    }

    public final void run() {
        this.f$0.m1091x85bd046b(this.f$1, this.f$2, this.f$3);
    }
}
