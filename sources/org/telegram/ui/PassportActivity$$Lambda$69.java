package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PassportActivity$$Lambda$69 implements RequestDelegate {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$69(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$10$PassportActivity(tLObject, tL_error);
    }
}
