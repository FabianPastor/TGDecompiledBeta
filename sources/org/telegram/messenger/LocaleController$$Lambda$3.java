package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_langPackDifference;

final /* synthetic */ class LocaleController$$Lambda$3 implements Runnable {
    private final LocaleController arg$1;
    private final String arg$2;
    private final TL_langPackDifference arg$3;
    private final HashMap arg$4;

    LocaleController$$Lambda$3(LocaleController localeController, String str, TL_langPackDifference tL_langPackDifference, HashMap hashMap) {
        this.arg$1 = localeController;
        this.arg$2 = str;
        this.arg$3 = tL_langPackDifference;
        this.arg$4 = hashMap;
    }

    public void run() {
        this.arg$1.lambda$saveRemoteLocaleStrings$3$LocaleController(this.arg$2, this.arg$3, this.arg$4);
    }
}
