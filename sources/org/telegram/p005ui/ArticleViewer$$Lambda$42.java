package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$42 */
final /* synthetic */ class ArticleViewer$$Lambda$42 implements Runnable {
    private final ArticleViewer arg$1;
    private final TLObject arg$2;
    private final String arg$3;
    private final TL_messages_getWebPage arg$4;

    ArticleViewer$$Lambda$42(ArticleViewer articleViewer, TLObject tLObject, String str, TL_messages_getWebPage tL_messages_getWebPage) {
        this.arg$1 = articleViewer;
        this.arg$2 = tLObject;
        this.arg$3 = str;
        this.arg$4 = tL_messages_getWebPage;
    }

    public void run() {
        this.arg$1.lambda$null$5$ArticleViewer(this.arg$2, this.arg$3, this.arg$4);
    }
}
