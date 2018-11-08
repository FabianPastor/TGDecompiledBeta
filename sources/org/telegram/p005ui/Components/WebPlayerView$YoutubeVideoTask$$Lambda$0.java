package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.WebPlayerView.YoutubeVideoTask;

/* renamed from: org.telegram.ui.Components.WebPlayerView$YoutubeVideoTask$$Lambda$0 */
final /* synthetic */ class WebPlayerView$YoutubeVideoTask$$Lambda$0 implements Runnable {
    private final YoutubeVideoTask arg$1;
    private final String arg$2;

    WebPlayerView$YoutubeVideoTask$$Lambda$0(YoutubeVideoTask youtubeVideoTask, String str) {
        this.arg$1 = youtubeVideoTask;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$doInBackground$1$WebPlayerView$YoutubeVideoTask(this.arg$2);
    }
}
