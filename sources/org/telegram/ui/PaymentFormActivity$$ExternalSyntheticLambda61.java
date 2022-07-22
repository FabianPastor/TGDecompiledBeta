package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda61 implements RequestDelegate {
    public static final /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda61 INSTANCE = new PaymentFormActivity$$ExternalSyntheticLambda61();

    private /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda61() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PaymentFormActivity.lambda$sendForm$51(tLObject, tLRPC$TL_error);
    }
}
