package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.PhoneConfirmationView.C19243.C19231;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$3$1$$Lambda$1 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$3$1$$Lambda$1 implements Runnable {
    private final C19231 arg$1;
    private final TL_error arg$2;

    PassportActivity$PhoneConfirmationView$3$1$$Lambda$1(C19231 c19231, TL_error tL_error) {
        this.arg$1 = c19231;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$PassportActivity$PhoneConfirmationView$3$1(this.arg$2);
    }
}
