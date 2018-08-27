package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView.C11243.C11231;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$3$1$$Lambda$1 implements Runnable {
    private final C11231 arg$1;
    private final TL_error arg$2;

    PassportActivity$PhoneConfirmationView$3$1$$Lambda$1(C11231 c11231, TL_error tL_error) {
        this.arg$1 = c11231;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$PassportActivity$PhoneConfirmationView$3$1(this.arg$2);
    }
}
