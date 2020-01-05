package org.telegram.messenger;

import org.telegram.messenger.TonController.StringCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$t6QAy4FxSz0TIxSldHqjrzP73cU implements StringCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ UserConfig f$1;

    public /* synthetic */ -$$Lambda$TonController$t6QAy4FxSz0TIxSldHqjrzP73cU(TonController tonController, UserConfig userConfig) {
        this.f$0 = tonController;
        this.f$1 = userConfig;
    }

    public final void run(String str) {
        this.f$0.lambda$loadTonConfigFromUrl$43$TonController(this.f$1, str);
    }
}
