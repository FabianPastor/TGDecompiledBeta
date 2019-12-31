package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$ANDCmubqTiuFsobBDpZALRnuNC4 implements Runnable {
    private final /* synthetic */ ErrorCallback f$0;

    public /* synthetic */ -$$Lambda$TonController$ANDCmubqTiuFsobBDpZALRnuNC4(ErrorCallback errorCallback) {
        this.f$0 = errorCallback;
    }

    public final void run() {
        this.f$0.run("TONLIB_INIT_FAIL", null);
    }
}
