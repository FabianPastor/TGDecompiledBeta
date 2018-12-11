package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.StickersActivity$$Lambda$1 */
final /* synthetic */ class StickersActivity$$Lambda$1 implements RequestDelegate {
    static final RequestDelegate $instance = new StickersActivity$$Lambda$1();

    private StickersActivity$$Lambda$1() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        StickersActivity.lambda$sendReorder$2$StickersActivity(tLObject, tL_error);
    }
}
