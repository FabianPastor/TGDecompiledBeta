package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda57 implements RequestDelegate {
    public static final /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda57 INSTANCE = new PaymentFormActivity$$ExternalSyntheticLambda57();

    private /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda57() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PaymentFormActivity.lambda$sendForm$51(tLObject, tL_error);
    }
}
