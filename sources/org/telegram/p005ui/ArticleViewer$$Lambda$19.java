package org.telegram.p005ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.WebPage;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$19 */
final /* synthetic */ class ArticleViewer$$Lambda$19 implements RequestDelegate {
    private final ArticleViewer arg$1;
    private final WebPage arg$2;
    private final MessageObject arg$3;
    private final String arg$4;
    private final int arg$5;

    ArticleViewer$$Lambda$19(ArticleViewer articleViewer, WebPage webPage, MessageObject messageObject, String str, int i) {
        this.arg$1 = articleViewer;
        this.arg$2 = webPage;
        this.arg$3 = messageObject;
        this.arg$4 = str;
        this.arg$5 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$open$23$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
