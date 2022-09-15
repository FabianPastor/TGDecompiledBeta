package org.telegram.ui.Components;

import android.webkit.WebResourceRequest;
import org.telegram.ui.Components.PhotoViewerWebView;

public final /* synthetic */ class PhotoViewerWebView$2$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ PhotoViewerWebView.AnonymousClass2 f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ WebResourceRequest f$2;

    public /* synthetic */ PhotoViewerWebView$2$$ExternalSyntheticLambda0(PhotoViewerWebView.AnonymousClass2 r1, String str, WebResourceRequest webResourceRequest) {
        this.f$0 = r1;
        this.f$1 = str;
        this.f$2 = webResourceRequest;
    }

    public final void run() {
        this.f$0.lambda$shouldInterceptRequest$0(this.f$1, this.f$2);
    }
}
