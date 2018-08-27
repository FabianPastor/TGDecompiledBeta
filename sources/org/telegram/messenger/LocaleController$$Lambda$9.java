package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class LocaleController$$Lambda$9 implements Runnable {
    private final LocaleController arg$1;
    private final TLObject arg$2;
    private final int arg$3;

    LocaleController$$Lambda$9(LocaleController localeController, TLObject tLObject, int i) {
        this.arg$1 = localeController;
        this.arg$2 = tLObject;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$4$LocaleController(this.arg$2, this.arg$3);
    }
}
