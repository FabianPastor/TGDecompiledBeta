package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.PaymentFormActivity$$Lambda$5 */
final /* synthetic */ class PaymentFormActivity$$Lambda$5 implements OnTouchListener {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$5(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createView$6$PaymentFormActivity(view, motionEvent);
    }
}
