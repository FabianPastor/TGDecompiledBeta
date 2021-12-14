package org.telegram.ui;

import org.telegram.tgnet.TLRPC$LangPackString;

public final /* synthetic */ class IntroActivity$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ IntroActivity f$0;
    public final /* synthetic */ TLRPC$LangPackString f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ IntroActivity$$ExternalSyntheticLambda2(IntroActivity introActivity, TLRPC$LangPackString tLRPC$LangPackString, String str) {
        this.f$0 = introActivity;
        this.f$1 = tLRPC$LangPackString;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$checkContinueText$2(this.f$1, this.f$2);
    }
}
