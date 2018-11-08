package org.telegram.p005ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Adapters.StickersAdapter$$Lambda$0 */
final /* synthetic */ class StickersAdapter$$Lambda$0 implements RequestDelegate {
    private final StickersAdapter arg$1;
    private final String arg$2;

    StickersAdapter$$Lambda$0(StickersAdapter stickersAdapter, String str) {
        this.arg$1 = stickersAdapter;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchServerStickers$1$StickersAdapter(this.arg$2, tLObject, tL_error);
    }
}
