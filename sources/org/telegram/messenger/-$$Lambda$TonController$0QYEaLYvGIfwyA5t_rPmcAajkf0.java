package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$0QYEaLYvGIfwyA5t_rPmcAajkf0 implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ ErrorCallback f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$TonController$0QYEaLYvGIfwyA5t_rPmcAajkf0(TonController tonController, String str, ErrorCallback errorCallback, Runnable runnable) {
        this.f$0 = tonController;
        this.f$1 = str;
        this.f$2 = errorCallback;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$prepareForPasscodeChange$24$TonController(this.f$1, this.f$2, this.f$3);
    }
}
