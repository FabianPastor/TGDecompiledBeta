package org.telegram.ui;

import org.telegram.messenger.SecureDocument;

final /* synthetic */ class PassportActivity$$Lambda$47 implements Runnable {
    private final PassportActivity arg$1;
    private final SecureDocument arg$2;
    private final int arg$3;

    PassportActivity$$Lambda$47(PassportActivity passportActivity, SecureDocument secureDocument, int i) {
        this.arg$1 = passportActivity;
        this.arg$2 = secureDocument;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$70$PassportActivity(this.arg$2, this.arg$3);
    }
}
