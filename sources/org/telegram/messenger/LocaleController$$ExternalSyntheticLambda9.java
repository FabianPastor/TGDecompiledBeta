package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ LocaleController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ LocaleController$$ExternalSyntheticLambda9(LocaleController localeController, TLObject tLObject, boolean z, int i) {
        this.f$0 = localeController;
        this.f$1 = tLObject;
        this.f$2 = z;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$loadRemoteLanguages$6(this.f$1, this.f$2, this.f$3);
    }
}
