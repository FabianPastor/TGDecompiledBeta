package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.EmbedBottomSheet.YoutubeProxy;

/* renamed from: org.telegram.ui.Components.EmbedBottomSheet$YoutubeProxy$$Lambda$0 */
final /* synthetic */ class EmbedBottomSheet$YoutubeProxy$$Lambda$0 implements Runnable {
    private final YoutubeProxy arg$1;
    private final String arg$2;

    EmbedBottomSheet$YoutubeProxy$$Lambda$0(YoutubeProxy youtubeProxy, String str) {
        this.arg$1 = youtubeProxy;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$postEvent$0$EmbedBottomSheet$YoutubeProxy(this.arg$2);
    }
}
