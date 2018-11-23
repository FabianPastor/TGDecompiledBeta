package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.TLRPC.TL_langPackDifference;

final /* synthetic */ class LocaleController$$Lambda$4 implements Runnable {
    private final LocaleController arg$1;
    private final LocaleInfo arg$2;
    private final int arg$3;
    private final TL_langPackDifference arg$4;
    private final HashMap arg$5;

    LocaleController$$Lambda$4(LocaleController localeController, LocaleInfo localeInfo, int i, TL_langPackDifference tL_langPackDifference, HashMap hashMap) {
        this.arg$1 = localeController;
        this.arg$2 = localeInfo;
        this.arg$3 = i;
        this.arg$4 = tL_langPackDifference;
        this.arg$5 = hashMap;
    }

    public void run() {
        this.arg$1.lambda$saveRemoteLocaleStrings$4$LocaleController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
