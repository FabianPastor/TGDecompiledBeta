package org.telegram.messenger;

import org.telegram.messenger.LocaleController.LocaleInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocaleController$rPqRyQsgkE1_kSvpx5ngOlMyIY4 implements Runnable {
    private final /* synthetic */ LocaleController f$0;
    private final /* synthetic */ LocaleInfo f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$LocaleController$rPqRyQsgkE1_kSvpx5ngOlMyIY4(LocaleController localeController, LocaleInfo localeInfo, int i) {
        this.f$0 = localeController;
        this.f$1 = localeInfo;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$applyLanguage$2$LocaleController(this.f$1, this.f$2);
    }
}
