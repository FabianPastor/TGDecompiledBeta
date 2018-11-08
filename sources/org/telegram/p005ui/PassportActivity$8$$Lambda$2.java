package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C19198;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$2 */
final /* synthetic */ class PassportActivity$8$$Lambda$2 implements RequestDelegate {
    private final C19198 arg$1;
    private final boolean arg$2;

    PassportActivity$8$$Lambda$2(C19198 c19198, boolean z) {
        this.arg$1 = c19198;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$10$PassportActivity$8(this.arg$2, tLObject, tL_error);
    }
}
