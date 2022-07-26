package org.telegram.ui;

import android.app.Activity;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.ui.ActionBar.ActionBarLayout;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda54 implements MessagesController.NewMessageCallback {
    public final /* synthetic */ PaymentFormActivity f$0;
    public final /* synthetic */ ActionBarLayout f$1;
    public final /* synthetic */ Activity f$2;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda54(PaymentFormActivity paymentFormActivity, ActionBarLayout actionBarLayout, Activity activity) {
        this.f$0 = paymentFormActivity;
        this.f$1 = actionBarLayout;
        this.f$2 = activity;
    }

    public final boolean onMessageReceived(TLRPC$Message tLRPC$Message) {
        return this.f$0.lambda$sendData$58(this.f$1, this.f$2, tLRPC$Message);
    }
}
