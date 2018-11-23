package org.telegram.p005ui;

import org.telegram.p005ui.CancelAccountDeletionActivity.LoginActivitySmsView.C04933.C04921;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$3$1$$Lambda$1 */
final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$3$1$$Lambda$1 implements Runnable {
    private final C04921 arg$1;
    private final TL_error arg$2;

    CancelAccountDeletionActivity$LoginActivitySmsView$3$1$$Lambda$1(C04921 c04921, TL_error tL_error) {
        this.arg$1 = c04921;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.mo9681x9cc5d18a(this.arg$2);
    }
}
