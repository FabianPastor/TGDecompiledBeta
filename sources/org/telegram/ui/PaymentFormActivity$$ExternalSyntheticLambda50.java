package org.telegram.ui;

import org.telegram.tgnet.TLRPC$account_Password;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda50 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ TLRPC$account_Password f$1;
    public final /* synthetic */ byte[] f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda50(PaymentFormActivity paymentFormActivity, TLRPC$account_Password tLRPC$account_Password, byte[] bArr) {
        this.f$0 = paymentFormActivity;
        this.f$1 = tLRPC$account_Password;
        this.f$2 = bArr;
    }

    public final void run() {
        this.f$0.lambda$checkPassword$64(this.f$1, this.f$2);
    }
}
