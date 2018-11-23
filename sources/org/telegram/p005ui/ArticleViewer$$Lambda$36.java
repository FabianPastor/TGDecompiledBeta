package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockChannelCell;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$36 */
final /* synthetic */ class ArticleViewer$$Lambda$36 implements Runnable {
    private final BlockChannelCell arg$1;

    ArticleViewer$$Lambda$36(BlockChannelCell blockChannelCell) {
        this.arg$1 = blockChannelCell;
    }

    public void run() {
        this.arg$1.setState(2, false);
    }
}
