package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;

final /* synthetic */ class DialogsSearchAdapter$$Lambda$11 implements Runnable {
    private final DialogsSearchAdapter arg$1;
    private final int arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final TL_messages_searchGlobal arg$5;

    DialogsSearchAdapter$$Lambda$11(DialogsSearchAdapter dialogsSearchAdapter, int i, TL_error tL_error, TLObject tLObject, TL_messages_searchGlobal tL_messages_searchGlobal) {
        this.arg$1 = dialogsSearchAdapter;
        this.arg$2 = i;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = tL_messages_searchGlobal;
    }

    public void run() {
        this.arg$1.lambda$null$0$DialogsSearchAdapter(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
