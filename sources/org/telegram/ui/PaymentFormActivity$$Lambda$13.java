package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class PaymentFormActivity$$Lambda$13 implements OnClickListener {
    private final PaymentFormActivity arg$1;
    private final String arg$2;
    private final String arg$3;

    PaymentFormActivity$$Lambda$13(PaymentFormActivity paymentFormActivity, String str, String str2) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$15$PaymentFormActivity(this.arg$2, this.arg$3, view);
    }
}
