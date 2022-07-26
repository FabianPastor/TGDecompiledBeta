package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_password;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$TL_account_password f$1;
    public final /* synthetic */ byte[] f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda42(PaymentFormActivity paymentFormActivity, TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$TL_account_password;
        this.f$2 = bArr;
    }

    public final void run() {
        this.f$0.lambda$checkPassword$64(this.f$1, this.f$2);
    }
}
