package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$73 */
final /* synthetic */ class PassportActivity$$Lambda$73 implements Runnable {
    private final PassportActivity arg$1;
    private final TLObject arg$2;

    PassportActivity$$Lambda$73(PassportActivity passportActivity, TLObject tLObject) {
        this.arg$1 = passportActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$3$PassportActivity(this.arg$2);
    }
}
