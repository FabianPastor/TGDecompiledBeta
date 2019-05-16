package org.telegram.messenger;

import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.TLObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocaleController$e6CsiR21q6PI2LDNrAzXcAWuMTk implements Runnable {
    private final /* synthetic */ LocaleController f$0;
    private final /* synthetic */ LocaleInfo f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$LocaleController$e6CsiR21q6PI2LDNrAzXcAWuMTk(LocaleController localeController, LocaleInfo localeInfo, TLObject tLObject, int i) {
        this.f$0 = localeController;
        this.f$1 = localeInfo;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$null$9$LocaleController(this.f$1, this.f$2, this.f$3);
    }
}
