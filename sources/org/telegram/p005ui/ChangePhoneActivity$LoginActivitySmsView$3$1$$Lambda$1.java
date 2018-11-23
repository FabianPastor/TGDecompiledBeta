package org.telegram.p005ui;

import org.telegram.p005ui.ChangePhoneActivity.LoginActivitySmsView.C05333.C05321;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$1 */
final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$1 implements Runnable {
    private final C05321 arg$1;
    private final TL_error arg$2;

    ChangePhoneActivity$LoginActivitySmsView$3$1$$Lambda$1(C05321 c05321, TL_error tL_error) {
        this.arg$1 = c05321;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$ChangePhoneActivity$LoginActivitySmsView$3$1(this.arg$2);
    }
}
