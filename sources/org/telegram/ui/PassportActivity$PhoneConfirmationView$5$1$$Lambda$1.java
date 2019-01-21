package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView.5.AnonymousClass1;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$5$1$$Lambda$1 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final TL_error arg$2;

    PassportActivity$PhoneConfirmationView$5$1$$Lambda$1(AnonymousClass1 anonymousClass1, TL_error tL_error) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$PassportActivity$PhoneConfirmationView$5$1(this.arg$2);
    }
}
