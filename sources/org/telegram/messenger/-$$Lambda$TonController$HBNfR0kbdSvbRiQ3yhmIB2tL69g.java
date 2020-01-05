package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$HBNfR0kbdSvbRiQ3yhmIB2tL69g implements Runnable {
    private final /* synthetic */ ErrorCallback f$0;

    public /* synthetic */ -$$Lambda$TonController$HBNfR0kbdSvbRiQ3yhmIB2tL69g(ErrorCallback errorCallback) {
        this.f$0 = errorCallback;
    }

    public final void run() {
        this.f$0.run("KEYSTORE_FAIL", null);
    }
}
