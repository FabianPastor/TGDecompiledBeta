package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda51 implements RequestDelegate {
    public static final /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda51 INSTANCE = new PaymentFormActivity$$ExternalSyntheticLambda51();

    private /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda51() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PaymentFormActivity.lambda$sendForm$46(tLObject, tL_error);
    }
}
