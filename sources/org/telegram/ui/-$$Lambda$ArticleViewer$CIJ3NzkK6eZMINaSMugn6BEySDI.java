package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$CIJ3NzkK6eZMINaSMugn6BEySDI implements RequestDelegate {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ TL_messages_getWebPage f$3;

    public /* synthetic */ -$$Lambda$ArticleViewer$CIJ3NzkK6eZMINaSMugn6BEySDI(ArticleViewer articleViewer, int i, String str, TL_messages_getWebPage tL_messages_getWebPage) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = tL_messages_getWebPage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$openWebpageUrl$6$ArticleViewer(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
