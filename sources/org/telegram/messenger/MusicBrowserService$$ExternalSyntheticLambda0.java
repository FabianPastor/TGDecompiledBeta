package org.telegram.messenger;

import android.service.media.MediaBrowserService;

public final /* synthetic */ class MusicBrowserService$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MusicBrowserService f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ MediaBrowserService.Result f$2;

    public /* synthetic */ MusicBrowserService$$ExternalSyntheticLambda0(MusicBrowserService musicBrowserService, String str, MediaBrowserService.Result result) {
        this.f$0 = musicBrowserService;
        this.f$1 = str;
        this.f$2 = result;
    }

    public final void run() {
        this.f$0.lambda$onLoadChildren$0(this.f$1, this.f$2);
    }
}
