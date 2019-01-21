package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass8;

final /* synthetic */ class PassportActivity$8$$Lambda$8 implements Runnable {
    private final AnonymousClass8 arg$1;
    private final TLObject arg$2;
    private final TL_error arg$3;

    PassportActivity$8$$Lambda$8(AnonymousClass8 anonymousClass8, TLObject tLObject, TL_error tL_error) {
        this.arg$1 = anonymousClass8;
        this.arg$2 = tLObject;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$12$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
