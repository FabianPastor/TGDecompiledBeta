package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.CLASSNAME;
import org.telegram.p005ui.PassportActivity.ErrorRunnable;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$5 */
final /* synthetic */ class PassportActivity$3$$Lambda$5 implements RequestDelegate {
    private final CLASSNAME arg$1;
    private final Runnable arg$2;
    private final ErrorRunnable arg$3;
    private final TL_account_verifyEmail arg$4;

    PassportActivity$3$$Lambda$5(CLASSNAME CLASSNAME, Runnable runnable, ErrorRunnable errorRunnable, TL_account_verifyEmail tL_account_verifyEmail) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = runnable;
        this.arg$3 = errorRunnable;
        this.arg$4 = tL_account_verifyEmail;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onItemClick$6$PassportActivity$3(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
