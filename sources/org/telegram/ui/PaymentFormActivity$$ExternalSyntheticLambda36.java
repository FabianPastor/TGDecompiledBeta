package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLRPC$TL_account_getTmpPassword f$3;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda36(PaymentFormActivity paymentFormActivity, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_getTmpPassword tLRPC$TL_account_getTmpPassword) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLRPC$TL_account_getTmpPassword;
    }

    public final void run() {
        this.f$0.lambda$checkPassword$54(this.f$1, this.f$2, this.f$3);
    }
}
