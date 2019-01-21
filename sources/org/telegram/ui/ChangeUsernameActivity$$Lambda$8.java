package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChangeUsernameActivity$$Lambda$8 implements Runnable {
    private final ChangeUsernameActivity arg$1;
    private final String arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;

    ChangeUsernameActivity$$Lambda$8(ChangeUsernameActivity changeUsernameActivity, String str, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = str;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$2$ChangeUsernameActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
