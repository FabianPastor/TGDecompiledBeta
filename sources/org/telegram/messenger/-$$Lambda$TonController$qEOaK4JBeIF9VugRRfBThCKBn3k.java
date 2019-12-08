package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$qEOaK4JBeIF9VugRRfBThCKBn3k implements RequestDelegate {
    private final /* synthetic */ TonController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$TonController$qEOaK4JBeIF9VugRRfBThCKBn3k(TonController tonController, long j) {
        this.f$0 = tonController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$0$TonController(this.f$1, tLObject, tL_error);
    }
}
