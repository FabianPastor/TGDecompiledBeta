package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass8;

final /* synthetic */ class PassportActivity$8$$Lambda$2 implements RequestDelegate {
    private final AnonymousClass8 arg$1;
    private final boolean arg$2;

    PassportActivity$8$$Lambda$2(AnonymousClass8 anonymousClass8, boolean z) {
        this.arg$1 = anonymousClass8;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$10$PassportActivity$8(this.arg$2, tLObject, tL_error);
    }
}
