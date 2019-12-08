package org.telegram.messenger;

import org.telegram.messenger.TonController.BooleanCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TonController$kQUHKz4hyFcAYU8txACWltCsDRc implements Runnable {
    private final /* synthetic */ BooleanCallback f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$TonController$kQUHKz4hyFcAYU8txACWltCsDRc(BooleanCallback booleanCallback, boolean z) {
        this.f$0 = booleanCallback;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
