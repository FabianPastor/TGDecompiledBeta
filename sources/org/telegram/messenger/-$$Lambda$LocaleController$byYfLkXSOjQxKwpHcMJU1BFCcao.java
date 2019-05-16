package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.TLRPC.TL_langPackDifference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocaleController$byYfLkXSOjQxKwpHcMJU1BFCcao implements Runnable {
    private final /* synthetic */ LocaleController f$0;
    private final /* synthetic */ LocaleInfo f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TL_langPackDifference f$3;
    private final /* synthetic */ HashMap f$4;

    public /* synthetic */ -$$Lambda$LocaleController$byYfLkXSOjQxKwpHcMJU1BFCcao(LocaleController localeController, LocaleInfo localeInfo, int i, TL_langPackDifference tL_langPackDifference, HashMap hashMap) {
        this.f$0 = localeController;
        this.f$1 = localeInfo;
        this.f$2 = i;
        this.f$3 = tL_langPackDifference;
        this.f$4 = hashMap;
    }

    public final void run() {
        this.f$0.lambda$saveRemoteLocaleStrings$4$LocaleController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
