package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$38 */
final /* synthetic */ class PaymentFormActivity$$Lambda$38 implements OnClickListener {
    private final PaymentFormActivity arg$1;
    private final String arg$2;

    PaymentFormActivity$$Lambda$38(PaymentFormActivity paymentFormActivity, String str) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$30$PaymentFormActivity(this.arg$2, dialogInterface, i);
    }
}
