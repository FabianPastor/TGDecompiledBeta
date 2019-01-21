package org.telegram.ui;

final /* synthetic */ class ArticleViewer$$Lambda$37 implements Runnable {
    private final BlockChannelCell arg$1;

    ArticleViewer$$Lambda$37(BlockChannelCell blockChannelCell) {
        this.arg$1 = blockChannelCell;
    }

    public void run() {
        this.arg$1.setState(2, false);
    }
}
