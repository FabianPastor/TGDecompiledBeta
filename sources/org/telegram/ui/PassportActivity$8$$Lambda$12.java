package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.C16488;

final /* synthetic */ class PassportActivity$8$$Lambda$12 implements RequestDelegate {
    private final C16488 arg$1;

    PassportActivity$8$$Lambda$12(C16488 c16488) {
        this.arg$1 = c16488;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$5$PassportActivity$8(tLObject, tL_error);
    }
}