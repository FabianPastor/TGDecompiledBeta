package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockChannelCell;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$36 */
final /* synthetic */ class ArticleViewer$$Lambda$36 implements Runnable {
    private final ArticleViewer arg$1;
    private final BlockChannelCell arg$2;
    private final int arg$3;
    private final TL_error arg$4;
    private final TL_channels_joinChannel arg$5;

    ArticleViewer$$Lambda$36(ArticleViewer articleViewer, BlockChannelCell blockChannelCell, int i, TL_error tL_error, TL_channels_joinChannel tL_channels_joinChannel) {
        this.arg$1 = articleViewer;
        this.arg$2 = blockChannelCell;
        this.arg$3 = i;
        this.arg$4 = tL_error;
        this.arg$5 = tL_channels_joinChannel;
    }

    public void run() {
        this.arg$1.lambda$null$32$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
