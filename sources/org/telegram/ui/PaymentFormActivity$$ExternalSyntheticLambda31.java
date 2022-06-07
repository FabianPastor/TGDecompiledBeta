package org.telegram.ui;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda31 implements OnCompleteListener {
    public final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda31(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final void onComplete(Task task) {
        this.f$0.lambda$initGooglePay$37(task);
    }
}
