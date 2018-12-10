package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$43 */
final /* synthetic */ class ArticleViewer$$Lambda$43 implements Runnable {
    private final ArticleViewer arg$1;
    private final int arg$2;
    private final TLObject arg$3;
    private final String arg$4;
    private final TL_messages_getWebPage arg$5;

    ArticleViewer$$Lambda$43(ArticleViewer articleViewer, int i, TLObject tLObject, String str, TL_messages_getWebPage tL_messages_getWebPage) {
        this.arg$1 = articleViewer;
        this.arg$2 = i;
        this.arg$3 = tLObject;
        this.arg$4 = str;
        this.arg$5 = tL_messages_getWebPage;
    }

    public void run() {
        this.arg$1.lambda$null$5$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
