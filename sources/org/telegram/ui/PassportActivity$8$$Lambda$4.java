package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.C16488;

final /* synthetic */ class PassportActivity$8$$Lambda$4 implements Runnable {
    private final C16488 arg$1;
    private final boolean arg$2;
    private final TL_error arg$3;

    PassportActivity$8$$Lambda$4(C16488 c16488, boolean z, TL_error tL_error) {
        this.arg$1 = c16488;
        this.arg$2 = z;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$run$16$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
