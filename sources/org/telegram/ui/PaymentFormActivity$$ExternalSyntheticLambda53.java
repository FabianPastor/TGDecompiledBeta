package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda53 implements MessagesController.NewMessageCallback {
    public final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda53(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final boolean onMessageReceived(TLRPC$Message tLRPC$Message) {
        return this.f$0.lambda$sendData$57(tLRPC$Message);
    }
}
