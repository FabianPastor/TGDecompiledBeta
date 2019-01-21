package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$44 implements RequestDelegate {
    static final RequestDelegate $instance = new PaymentFormActivity$$Lambda$44();

    private PaymentFormActivity$$Lambda$44() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        PaymentFormActivity.lambda$null$17$PaymentFormActivity(tLObject, tL_error);
    }
}
