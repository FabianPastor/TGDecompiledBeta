package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.WebPage;

final /* synthetic */ class ArticleViewer$$Lambda$40 implements Runnable {
    private final ArticleViewer arg$1;
    private final WebPage arg$2;
    private final TL_webPage arg$3;
    private final MessageObject arg$4;
    private final String arg$5;

    ArticleViewer$$Lambda$40(ArticleViewer articleViewer, WebPage webPage, TL_webPage tL_webPage, MessageObject messageObject, String str) {
        this.arg$1 = articleViewer;
        this.arg$2 = webPage;
        this.arg$3 = tL_webPage;
        this.arg$4 = messageObject;
        this.arg$5 = str;
    }

    public void run() {
        this.arg$1.lambda$null$22$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}