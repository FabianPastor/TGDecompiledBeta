package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.PhoneConfirmationView.CLASSNAME.CLASSNAME;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$3$1$$Lambda$1 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$3$1$$Lambda$1 implements Runnable {
    private final CLASSNAME arg$1;
    private final TL_error arg$2;

    PassportActivity$PhoneConfirmationView$3$1$$Lambda$1(CLASSNAME CLASSNAME, TL_error tL_error) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$PassportActivity$PhoneConfirmationView$3$1(this.arg$2);
    }
}
