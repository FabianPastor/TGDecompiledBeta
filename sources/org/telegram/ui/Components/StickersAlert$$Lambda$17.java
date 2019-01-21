package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;

final /* synthetic */ class StickersAlert$$Lambda$17 implements Runnable {
    private final StickersAlert arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final TL_messages_getAttachedStickers arg$4;

    StickersAlert$$Lambda$17(StickersAlert stickersAlert, TL_error tL_error, TLObject tLObject, TL_messages_getAttachedStickers tL_messages_getAttachedStickers) {
        this.arg$1 = stickersAlert;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = tL_messages_getAttachedStickers;
    }

    public void run() {
        this.arg$1.lambda$null$0$StickersAlert(this.arg$2, this.arg$3, this.arg$4);
    }
}
