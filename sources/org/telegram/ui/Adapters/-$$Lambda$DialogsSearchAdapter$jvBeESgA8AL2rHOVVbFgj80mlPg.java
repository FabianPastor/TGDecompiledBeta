package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsSearchAdapter$jvBeESgA8AL2rHOVVbFgj80mlPg implements RequestDelegate {
    private final /* synthetic */ DialogsSearchAdapter f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_messages_searchGlobal f$2;

    public /* synthetic */ -$$Lambda$DialogsSearchAdapter$jvBeESgA8AL2rHOVVbFgj80mlPg(DialogsSearchAdapter dialogsSearchAdapter, int i, TL_messages_searchGlobal tL_messages_searchGlobal) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = i;
        this.f$2 = tL_messages_searchGlobal;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchMessagesInternal$1$DialogsSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
    }
}
