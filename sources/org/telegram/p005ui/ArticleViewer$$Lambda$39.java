package org.telegram.p005ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.WebPage;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$39 */
final /* synthetic */ class ArticleViewer$$Lambda$39 implements Runnable {
    private final ArticleViewer arg$1;
    private final WebPage arg$2;
    private final TL_webPage arg$3;
    private final MessageObject arg$4;

    ArticleViewer$$Lambda$39(ArticleViewer articleViewer, WebPage webPage, TL_webPage tL_webPage, MessageObject messageObject) {
        this.arg$1 = articleViewer;
        this.arg$2 = webPage;
        this.arg$3 = tL_webPage;
        this.arg$4 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$null$21$ArticleViewer(this.arg$2, this.arg$3, this.arg$4);
    }
}
