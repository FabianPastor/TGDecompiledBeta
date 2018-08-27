package org.telegram.ui.Components;

import android.webkit.ValueCallback;

final /* synthetic */ class WebPlayerView$YoutubeVideoTask$$Lambda$1 implements ValueCallback {
    private final YoutubeVideoTask arg$1;

    WebPlayerView$YoutubeVideoTask$$Lambda$1(YoutubeVideoTask youtubeVideoTask) {
        this.arg$1 = youtubeVideoTask;
    }

    public void onReceiveValue(Object obj) {
        this.arg$1.lambda$null$0$WebPlayerView$YoutubeVideoTask((String) obj);
    }
}
