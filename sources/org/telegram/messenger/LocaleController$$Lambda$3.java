package org.telegram.messenger;

final /* synthetic */ class LocaleController$$Lambda$3 implements Runnable {
    private final LocaleController arg$1;
    private final int arg$2;

    LocaleController$$Lambda$3(LocaleController localeController, int i) {
        this.arg$1 = localeController;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$applyLanguage$3$LocaleController(this.arg$2);
    }
}
