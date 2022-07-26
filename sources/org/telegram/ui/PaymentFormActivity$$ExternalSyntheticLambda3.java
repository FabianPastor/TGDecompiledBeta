package org.telegram.ui;

import android.content.DialogInterface;
import java.util.List;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ List f$3;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda3(PaymentFormActivity paymentFormActivity, Runnable runnable, List list, List list2) {
        this.f$0 = paymentFormActivity;
        this.f$1 = runnable;
        this.f$2 = list;
        this.f$3 = list2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showChoosePaymentMethod$31(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
