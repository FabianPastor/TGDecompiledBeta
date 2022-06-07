package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda48(PaymentFormActivity paymentFormActivity, TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, String str) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.lambda$sendSavePassword$45(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
