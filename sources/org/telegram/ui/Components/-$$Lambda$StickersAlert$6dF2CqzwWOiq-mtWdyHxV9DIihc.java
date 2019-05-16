package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StickersAlert$6dF2CqzwWOiq-mtWdyHxV9DIihc implements Runnable {
    private final /* synthetic */ StickersAlert f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$StickersAlert$6dF2CqzwWOiq-mtWdyHxV9DIihc(StickersAlert stickersAlert, TL_error tL_error, TLObject tLObject) {
        this.f$0 = stickersAlert;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$3$StickersAlert(this.f$1, this.f$2);
    }
}
