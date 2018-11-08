package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C19198;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$1 */
final /* synthetic */ class PassportActivity$8$$Lambda$1 implements Runnable {
    private final C19198 arg$1;
    private final byte[] arg$2;
    private final String arg$3;

    PassportActivity$8$$Lambda$1(C19198 c19198, byte[] bArr, String str) {
        this.arg$1 = c19198;
        this.arg$2 = bArr;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$generateNewSecret$8$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
