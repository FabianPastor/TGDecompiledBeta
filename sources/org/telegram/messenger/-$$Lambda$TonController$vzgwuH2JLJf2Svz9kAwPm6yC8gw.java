package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.TonController.GetTransactionsCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$vzgwuH2JLJf2Svz9kAwPm6yC8gw implements Runnable {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ GetTransactionsCallback f$3;

    public /* synthetic */ -$$Lambda$TonController$vzgwuH2JLJf2Svz9kAwPm6yC8gw(TonController tonController, ArrayList arrayList, boolean z, GetTransactionsCallback getTransactionsCallback) {
        this.f$0 = tonController;
        this.f$1 = arrayList;
        this.f$2 = z;
        this.f$3 = getTransactionsCallback;
    }

    public final void run() {
        this.f$0.lambda$null$15$TonController(this.f$1, this.f$2, this.f$3);
    }
}
