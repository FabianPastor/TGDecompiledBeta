package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PassportActivity$$Lambda$68 implements Runnable {
    private final PassportActivity arg$1;
    private final TL_error arg$2;

    PassportActivity$$Lambda$68(PassportActivity passportActivity, TL_error tL_error) {
        this.arg$1 = passportActivity;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$14$PassportActivity(this.arg$2);
    }
}
