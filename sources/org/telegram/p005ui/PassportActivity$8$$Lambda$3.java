package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.C14998;
import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.PassportActivity$8$$Lambda$3 */
final /* synthetic */ class PassportActivity$8$$Lambda$3 implements Runnable {
    private final C14998 arg$1;
    private final TLObject arg$2;
    private final String arg$3;
    private final boolean arg$4;

    PassportActivity$8$$Lambda$3(C14998 c14998, TLObject tLObject, String str, boolean z) {
        this.arg$1 = c14998;
        this.arg$2 = tLObject;
        this.arg$3 = str;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$run$15$PassportActivity$8(this.arg$2, this.arg$3, this.arg$4);
    }
}
