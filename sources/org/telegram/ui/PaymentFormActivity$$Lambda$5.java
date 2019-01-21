package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class PaymentFormActivity$$Lambda$5 implements OnTouchListener {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$5(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createView$6$PaymentFormActivity(view, motionEvent);
    }
}
