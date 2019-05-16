package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsSearchAdapter$tzGJ1M1cHM4tSDBBFCCxa18ZhzA implements Runnable {
    private final /* synthetic */ DialogsSearchAdapter f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ TL_messages_searchGlobal f$4;

    public /* synthetic */ -$$Lambda$DialogsSearchAdapter$tzGJ1M1cHM4tSDBBFCCxa18ZhzA(DialogsSearchAdapter dialogsSearchAdapter, int i, TL_error tL_error, TLObject tLObject, TL_messages_searchGlobal tL_messages_searchGlobal) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = tL_messages_searchGlobal;
    }

    public final void run() {
        this.f$0.lambda$null$0$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
