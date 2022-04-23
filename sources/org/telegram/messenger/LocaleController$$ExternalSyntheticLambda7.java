package org.telegram.messenger;

import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ LocaleController f$0;
    public final /* synthetic */ LocaleController.LocaleInfo f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ LocaleController$$ExternalSyntheticLambda7(LocaleController localeController, LocaleController.LocaleInfo localeInfo, TLObject tLObject, int i) {
        this.f$0 = localeController;
        this.f$1 = localeInfo;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$applyRemoteLanguage$10(this.f$1, this.f$2, this.f$3);
    }
}
