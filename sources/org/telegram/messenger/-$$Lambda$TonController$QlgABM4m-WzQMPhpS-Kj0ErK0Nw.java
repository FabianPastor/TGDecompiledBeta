package org.telegram.messenger;

import drinkless.org.ton.TonApi.InputKey;
import org.telegram.messenger.TonController.DangerousCallback;
import org.telegram.messenger.TonController.ErrorCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$QlgABM4m-WzQMPhpS-Kj0ErK0Nw implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ Object f$1;
    private final /* synthetic */ DangerousCallback f$2;
    private final /* synthetic */ InputKey f$3;
    private final /* synthetic */ ErrorCallback f$4;

    public /* synthetic */ -$$Lambda$TonController$QlgABM4m-WzQMPhpS-Kj0ErK0Nw(TonController tonController, Object obj, DangerousCallback dangerousCallback, InputKey inputKey, ErrorCallback errorCallback) {
        this.f$0 = tonController;
        this.f$1 = obj;
        this.f$2 = dangerousCallback;
        this.f$3 = inputKey;
        this.f$4 = errorCallback;
    }

    public final void run() {
        this.f$0.lambda$null$40$TonController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}