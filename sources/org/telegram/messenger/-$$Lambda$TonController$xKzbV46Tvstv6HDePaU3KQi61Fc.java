package org.telegram.messenger;

import org.telegram.messenger.TonController.GetTransactionsCallback;
import org.telegram.messenger.TonController.TonLibCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$xKzbV46Tvstv6HDePaU3KQi61Fc implements TonLibCallback {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ GetTransactionsCallback f$2;

    public /* synthetic */ -$$Lambda$TonController$xKzbV46Tvstv6HDePaU3KQi61Fc(TonController tonController, boolean z, GetTransactionsCallback getTransactionsCallback) {
        this.f$0 = tonController;
        this.f$1 = z;
        this.f$2 = getTransactionsCallback;
    }

    public final void run(Object obj) {
        this.f$0.lambda$getTransactions$17$TonController(this.f$1, this.f$2, obj);
    }
}
