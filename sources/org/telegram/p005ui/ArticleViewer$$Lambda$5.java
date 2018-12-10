package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$5 */
final /* synthetic */ class ArticleViewer$$Lambda$5 implements RequestDelegate {
    private final ArticleViewer arg$1;
    private final int arg$2;
    private final String arg$3;
    private final TL_messages_getWebPage arg$4;

    ArticleViewer$$Lambda$5(ArticleViewer articleViewer, int i, String str, TL_messages_getWebPage tL_messages_getWebPage) {
        this.arg$1 = articleViewer;
        this.arg$2 = i;
        this.arg$3 = str;
        this.arg$4 = tL_messages_getWebPage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$openWebpageUrl$6$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
