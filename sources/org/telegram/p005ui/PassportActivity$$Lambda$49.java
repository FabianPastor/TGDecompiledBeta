package org.telegram.p005ui;

import org.telegram.messenger.MrzRecognizer.Result;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$49 */
final /* synthetic */ class PassportActivity$$Lambda$49 implements Runnable {
    private final PassportActivity arg$1;
    private final Result arg$2;

    PassportActivity$$Lambda$49(PassportActivity passportActivity, Result result) {
        this.arg$1 = passportActivity;
        this.arg$2 = result;
    }

    public void run() {
        this.arg$1.lambda$null$72$PassportActivity(this.arg$2);
    }
}
