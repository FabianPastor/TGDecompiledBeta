package org.telegram.ui.Components;

import org.telegram.ui.Components.PhotoViewerWebView;

public final /* synthetic */ class PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ PhotoViewerWebView.YoutubeProxy f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PhotoViewerWebView$YoutubeProxy$$ExternalSyntheticLambda1(PhotoViewerWebView.YoutubeProxy youtubeProxy, boolean z, int i) {
        this.f$0 = youtubeProxy;
        this.f$1 = z;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onPlayerStateChange$1(this.f$1, this.f$2);
    }
}
