package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C21978;
import org.telegram.tgnet.TLRPC.TL_account_passwordSettings;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$6 */
final /* synthetic */ class PassportActivity$8$$Lambda$6 implements Runnable {
    private final C21978 arg$1;
    private final TL_account_passwordSettings arg$2;
    private final boolean arg$3;
    private final byte[] arg$4;

    PassportActivity$8$$Lambda$6(C21978 c21978, TL_account_passwordSettings tL_account_passwordSettings, boolean z, byte[] bArr) {
        this.arg$1 = c21978;
        this.arg$2 = tL_account_passwordSettings;
        this.arg$3 = z;
        this.arg$4 = bArr;
    }

    public void run() {
        this.arg$1.lambda$null$14$PassportActivity$8(this.arg$2, this.arg$3, this.arg$4);
    }
}
