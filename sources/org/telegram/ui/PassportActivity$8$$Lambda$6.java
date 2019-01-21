package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_passwordSettings;
import org.telegram.ui.PassportActivity.AnonymousClass8;

final /* synthetic */ class PassportActivity$8$$Lambda$6 implements Runnable {
    private final AnonymousClass8 arg$1;
    private final TL_account_passwordSettings arg$2;
    private final boolean arg$3;
    private final byte[] arg$4;

    PassportActivity$8$$Lambda$6(AnonymousClass8 anonymousClass8, TL_account_passwordSettings tL_account_passwordSettings, boolean z, byte[] bArr) {
        this.arg$1 = anonymousClass8;
        this.arg$2 = tL_account_passwordSettings;
        this.arg$3 = z;
        this.arg$4 = bArr;
    }

    public void run() {
        this.arg$1.lambda$null$14$PassportActivity$8(this.arg$2, this.arg$3, this.arg$4);
    }
}
