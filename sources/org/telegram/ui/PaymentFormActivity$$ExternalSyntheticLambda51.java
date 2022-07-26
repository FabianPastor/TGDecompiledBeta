package org.telegram.ui;

import android.app.Activity;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.ui.ActionBar.ActionBarLayout;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda51 implements Runnable {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ ActionBarLayout f$1;
    public final /* synthetic */ Activity f$2;
    public final /* synthetic */ TLRPC$Message f$3;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda51(PaymentFormActivity paymentFormActivity, ActionBarLayout actionBarLayout, Activity activity, TLRPC$Message tLRPC$Message) {
        this.f$0 = paymentFormActivity;
        this.f$1 = actionBarLayout;
        this.f$2 = activity;
        this.f$3 = tLRPC$Message;
    }

    public final void run() {
        this.f$0.lambda$sendData$57(this.f$1, this.f$2, this.f$3);
    }
}
