package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$VzziGibbmz1VChBvlPo1LDLCXbs implements Runnable {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$SessionsActivity$VzziGibbmz1VChBvlPo1LDLCXbs(SessionsActivity sessionsActivity, TL_error tL_error, TLObject tLObject) {
        this.f$0 = sessionsActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$14$SessionsActivity(this.f$1, this.f$2);
    }
}