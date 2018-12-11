package org.telegram.messenger;

import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.TLObject;

final /* synthetic */ class LocaleController$$Lambda$13 implements Runnable {
    private final LocaleController arg$1;
    private final LocaleInfo arg$2;
    private final TLObject arg$3;
    private final int arg$4;

    LocaleController$$Lambda$13(LocaleController localeController, LocaleInfo localeInfo, TLObject tLObject, int i) {
        this.arg$1 = localeController;
        this.arg$2 = localeInfo;
        this.arg$3 = tLObject;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$null$7$LocaleController(this.arg$2, this.arg$3, this.arg$4);
    }
}
