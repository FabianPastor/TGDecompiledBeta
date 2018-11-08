package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$1 */
final /* synthetic */ class PaymentFormActivity$$Lambda$1 implements OnTouchListener {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$1(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createView$1$PaymentFormActivity(view, motionEvent);
    }
}
