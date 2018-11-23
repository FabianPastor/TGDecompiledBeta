package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$19 */
final /* synthetic */ class PaymentFormActivity$$Lambda$19 implements OnClickListener {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$19(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showPayAlert$25$PaymentFormActivity(dialogInterface, i);
    }
}
