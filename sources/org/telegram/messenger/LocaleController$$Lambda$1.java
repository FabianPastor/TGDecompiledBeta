package org.telegram.messenger;

import org.telegram.messenger.LocaleController.LocaleInfo;

final /* synthetic */ class LocaleController$$Lambda$1 implements Runnable {
    private final LocaleController arg$1;
    private final LocaleInfo arg$2;
    private final int arg$3;

    LocaleController$$Lambda$1(LocaleController localeController, LocaleInfo localeInfo, int i) {
        this.arg$1 = localeController;
        this.arg$2 = localeInfo;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$applyLanguage$1$LocaleController(this.arg$2, this.arg$3);
    }
}
