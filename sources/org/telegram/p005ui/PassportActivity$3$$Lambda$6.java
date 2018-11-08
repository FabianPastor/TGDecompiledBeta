package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C19143;
import org.telegram.p005ui.PassportActivity.ErrorRunnable;
import org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$6 */
final /* synthetic */ class PassportActivity$3$$Lambda$6 implements Runnable {
    private final C19143 arg$1;
    private final TL_error arg$2;
    private final Runnable arg$3;
    private final ErrorRunnable arg$4;
    private final TL_account_verifyEmail arg$5;

    PassportActivity$3$$Lambda$6(C19143 c19143, TL_error tL_error, Runnable runnable, ErrorRunnable errorRunnable, TL_account_verifyEmail tL_account_verifyEmail) {
        this.arg$1 = c19143;
        this.arg$2 = tL_error;
        this.arg$3 = runnable;
        this.arg$4 = errorRunnable;
        this.arg$5 = tL_account_verifyEmail;
    }

    public void run() {
        this.arg$1.lambda$null$5$PassportActivity$3(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
