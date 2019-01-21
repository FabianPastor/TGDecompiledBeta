package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class IntroActivity$$Lambda$3 implements RequestDelegate {
    private final IntroActivity arg$1;
    private final String arg$2;

    IntroActivity$$Lambda$3(IntroActivity introActivity, String str) {
        this.arg$1 = introActivity;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$checkContinueText$4$IntroActivity(this.arg$2, tLObject, tL_error);
    }
}
