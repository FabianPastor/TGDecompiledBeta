package org.telegram.ui;

import org.telegram.ui.PassportActivity.C15298;

final /* synthetic */ class PassportActivity$8$$Lambda$1 implements Runnable {
    private final C15298 arg$1;
    private final byte[] arg$2;
    private final String arg$3;

    PassportActivity$8$$Lambda$1(C15298 c15298, byte[] bArr, String str) {
        this.arg$1 = c15298;
        this.arg$2 = bArr;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$generateNewSecret$8$PassportActivity$8(this.arg$2, this.arg$3);
    }
}
