package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$3-4CkmOmoT2bO3dO2BASk7HXPCU implements Runnable {
    private final /* synthetic */ ErrorCallback f$0;

    public /* synthetic */ -$$Lambda$TonController$3-4CkmOmoT2bO3dO2BASk7HXPCU(ErrorCallback errorCallback) {
        this.f$0 = errorCallback;
    }

    public final void run() {
        this.f$0.run("KEYSTORE_FAIL_DECRYPT", null);
    }
}
