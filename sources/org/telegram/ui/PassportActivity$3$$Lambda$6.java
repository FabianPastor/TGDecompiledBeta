package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass3;

final /* synthetic */ class PassportActivity$3$$Lambda$6 implements Runnable {
    private final AnonymousClass3 arg$1;
    private final TL_error arg$2;
    private final Runnable arg$3;
    private final ErrorRunnable arg$4;
    private final TL_account_verifyEmail arg$5;

    PassportActivity$3$$Lambda$6(AnonymousClass3 anonymousClass3, TL_error tL_error, Runnable runnable, ErrorRunnable errorRunnable, TL_account_verifyEmail tL_account_verifyEmail) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = tL_error;
        this.arg$3 = runnable;
        this.arg$4 = errorRunnable;
        this.arg$5 = tL_account_verifyEmail;
    }

    public void run() {
        this.arg$1.lambda$null$5$PassportActivity$3(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
