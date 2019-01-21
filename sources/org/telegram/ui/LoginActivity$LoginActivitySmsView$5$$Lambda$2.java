package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView.AnonymousClass5;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$5$$Lambda$2 implements Runnable {
    private final AnonymousClass5 arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivitySmsView$5$$Lambda$2(AnonymousClass5 anonymousClass5, TL_error tL_error) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$LoginActivity$LoginActivitySmsView$5(this.arg$2);
    }
}
