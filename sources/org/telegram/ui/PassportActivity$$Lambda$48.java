package org.telegram.ui;

import org.telegram.messenger.MrzRecognizer.Result;

final /* synthetic */ class PassportActivity$$Lambda$48 implements Runnable {
    private final PassportActivity arg$1;
    private final Result arg$2;

    PassportActivity$$Lambda$48(PassportActivity passportActivity, Result result) {
        this.arg$1 = passportActivity;
        this.arg$2 = result;
    }

    public void run() {
        this.arg$1.lambda$null$71$PassportActivity(this.arg$2);
    }
}
