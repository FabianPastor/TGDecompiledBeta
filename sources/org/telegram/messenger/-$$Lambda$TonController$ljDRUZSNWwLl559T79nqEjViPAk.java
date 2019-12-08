package org.telegram.messenger;

import org.telegram.messenger.TonController.ErrorCallback;
import org.telegram.tgnet.TLObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$ljDRUZSNWwLl559T79nqEjViPAk implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ ErrorCallback f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$TonController$ljDRUZSNWwLl559T79nqEjViPAk(TonController tonController, ErrorCallback errorCallback, TLObject tLObject) {
        this.f$0 = tonController;
        this.f$1 = errorCallback;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$9$TonController(this.f$1, this.f$2);
    }
}
