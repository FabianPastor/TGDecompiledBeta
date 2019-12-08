package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$j-02fxwQTvT3M7lq3c6yrY1npPM implements Runnable {
    private final /* synthetic */ ErrorCallback f$0;

    public /* synthetic */ -$$Lambda$TonController$j-02fxwQTvT3M7lq3c6yrY1npPM(ErrorCallback errorCallback) {
        this.f$0 = errorCallback;
    }

    public final void run() {
        this.f$0.run("SALT_GET_FAIL", null);
    }
}
