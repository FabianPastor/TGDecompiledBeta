package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.WebPage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$z8m1_SXnv-7lkJNDb6qlQTHMjFc implements RequestDelegate {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ WebPage f$1;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$ArticleViewer$z8m1_SXnv-7lkJNDb6qlQTHMjFc(ArticleViewer articleViewer, WebPage webPage, MessageObject messageObject, String str, int i) {
        this.f$0 = articleViewer;
        this.f$1 = webPage;
        this.f$2 = messageObject;
        this.f$3 = str;
        this.f$4 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$open$23$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
