package org.telegram.ui.Adapters;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class StickersAdapter$$Lambda$2 implements Runnable {
    private final StickersAdapter arg$1;
    private final String arg$2;
    private final TLObject arg$3;

    StickersAdapter$$Lambda$2(StickersAdapter stickersAdapter, String str, TLObject tLObject) {
        this.arg$1 = stickersAdapter;
        this.arg$2 = str;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$2$StickersAdapter(this.arg$2, this.arg$3);
    }
}
