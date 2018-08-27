package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.C16488;

final /* synthetic */ class PassportActivity$8$$Lambda$14 implements Runnable {
    private final C16488 arg$1;
    private final TL_error arg$2;

    PassportActivity$8$$Lambda$14(C16488 c16488, TL_error tL_error) {
        this.arg$1 = c16488;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$2$PassportActivity$8(this.arg$2);
    }
}
