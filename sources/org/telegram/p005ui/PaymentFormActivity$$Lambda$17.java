package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$17 */
final /* synthetic */ class PaymentFormActivity$$Lambda$17 implements OnClickListener {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$17(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showPayAlert$22$PaymentFormActivity(dialogInterface, i);
    }
}
