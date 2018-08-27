package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.C16488;

final /* synthetic */ class PassportActivity$8$$Lambda$2 implements RequestDelegate {
    private final C16488 arg$1;
    private final boolean arg$2;

    PassportActivity$8$$Lambda$2(C16488 c16488, boolean z) {
        this.arg$1 = c16488;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$10$PassportActivity$8(this.arg$2, tLObject, tL_error);
    }
}
