package org.telegram.p005ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$$Lambda$0 */
final /* synthetic */ class BaseLocationAdapter$$Lambda$0 implements RequestDelegate {
    private final BaseLocationAdapter arg$1;

    BaseLocationAdapter$$Lambda$0(BaseLocationAdapter baseLocationAdapter) {
        this.arg$1 = baseLocationAdapter;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchBotUser$1$BaseLocationAdapter(tLObject, tL_error);
    }
}
