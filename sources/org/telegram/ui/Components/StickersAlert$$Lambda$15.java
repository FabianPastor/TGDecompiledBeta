package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class StickersAlert$$Lambda$15 implements Runnable {
    private final StickersAlert arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    StickersAlert$$Lambda$15(StickersAlert stickersAlert, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = stickersAlert;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$13$StickersAlert(this.arg$2, this.arg$3);
    }
}
