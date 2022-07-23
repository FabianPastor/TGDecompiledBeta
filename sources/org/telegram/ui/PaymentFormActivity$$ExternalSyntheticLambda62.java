package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda62 implements RequestDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda62(PaymentFormActivity paymentFormActivity, boolean z, String str) {
        this.f$0 = paymentFormActivity;
        this.f$1 = z;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendSavePassword$46(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
