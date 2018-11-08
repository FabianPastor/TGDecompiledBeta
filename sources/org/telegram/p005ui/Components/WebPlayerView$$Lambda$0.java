package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.WebPlayerView.CallJavaResultInterface;

/* renamed from: org.telegram.ui.Components.WebPlayerView$$Lambda$0 */
final /* synthetic */ class WebPlayerView$$Lambda$0 implements CallJavaResultInterface {
    private final WebPlayerView arg$1;

    WebPlayerView$$Lambda$0(WebPlayerView webPlayerView) {
        this.arg$1 = webPlayerView;
    }

    public void jsCallFinished(String str) {
        this.arg$1.lambda$new$0$WebPlayerView(str);
    }
}
