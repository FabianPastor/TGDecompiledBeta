package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StickersAlert$NU6Lj7s7oms1kuoHZ-7sXpiftZ4 implements Runnable {
    private final /* synthetic */ StickersAlert f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_messages_getAttachedStickers f$3;

    public /* synthetic */ -$$Lambda$StickersAlert$NU6Lj7s7oms1kuoHZ-7sXpiftZ4(StickersAlert stickersAlert, TL_error tL_error, TLObject tLObject, TL_messages_getAttachedStickers tL_messages_getAttachedStickers) {
        this.f$0 = stickersAlert;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_messages_getAttachedStickers;
    }

    public final void run() {
        this.f$0.lambda$null$0$StickersAlert(this.f$1, this.f$2, this.f$3);
    }
}
