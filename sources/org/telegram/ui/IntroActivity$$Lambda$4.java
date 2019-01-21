package org.telegram.ui;

import org.telegram.tgnet.TLRPC.LangPackString;

final /* synthetic */ class IntroActivity$$Lambda$4 implements Runnable {
    private final IntroActivity arg$1;
    private final LangPackString arg$2;
    private final String arg$3;

    IntroActivity$$Lambda$4(IntroActivity introActivity, LangPackString langPackString, String str) {
        this.arg$1 = introActivity;
        this.arg$2 = langPackString;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$null$3$IntroActivity(this.arg$2, this.arg$3);
    }
}
