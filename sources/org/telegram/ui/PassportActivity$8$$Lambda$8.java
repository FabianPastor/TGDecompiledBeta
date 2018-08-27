package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.C16488;

final /* synthetic */ class PassportActivity$8$$Lambda$8 implements Runnable {
    private final C16488 arg$1;
    private final TLObject arg$2;
    private final TL_error arg$3;

    PassportActivity$8$$Lambda$8(C16488 c16488, TLObject tLObject, TL_error tL_error) {
        this.arg$1 = c16488;
        this.arg$2 = tLObject;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$12$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
