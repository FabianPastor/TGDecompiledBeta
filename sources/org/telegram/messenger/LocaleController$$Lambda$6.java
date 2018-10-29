package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LocaleController$$Lambda$6 implements RequestDelegate {
    private final LocaleController arg$1;
    private final int arg$2;

    LocaleController$$Lambda$6(LocaleController localeController, int i) {
        this.arg$1 = localeController;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$applyRemoteLanguage$9$LocaleController(this.arg$2, tLObject, tL_error);
    }
}
