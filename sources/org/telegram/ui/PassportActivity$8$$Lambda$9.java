package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass8;

final /* synthetic */ class PassportActivity$8$$Lambda$9 implements Runnable {
    private final AnonymousClass8 arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final boolean arg$4;

    PassportActivity$8$$Lambda$9(AnonymousClass8 anonymousClass8, TL_error tL_error, TLObject tLObject, boolean z) {
        this.arg$1 = anonymousClass8;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$null$9$PassportActivity$8(this.arg$2, this.arg$3, this.arg$4);
    }
}
