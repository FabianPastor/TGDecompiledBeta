package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$6 */
final /* synthetic */ class PaymentFormActivity$$Lambda$6 implements OnEditorActionListener {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$6(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$7$PaymentFormActivity(textView, i, keyEvent);
    }
}
