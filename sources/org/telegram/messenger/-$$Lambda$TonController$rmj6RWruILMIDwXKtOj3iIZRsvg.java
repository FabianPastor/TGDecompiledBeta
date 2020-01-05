package org.telegram.messenger;

import org.telegram.messenger.TonController.FeeCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$rmj6RWruILMIDwXKtOj3iIZRsvg implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ FeeCallback f$5;

    public /* synthetic */ -$$Lambda$TonController$rmj6RWruILMIDwXKtOj3iIZRsvg(TonController tonController, String str, String str2, long j, String str3, FeeCallback feeCallback) {
        this.f$0 = tonController;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = j;
        this.f$4 = str3;
        this.f$5 = feeCallback;
    }

    public final void run() {
        this.f$0.lambda$getSendFee$36$TonController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
