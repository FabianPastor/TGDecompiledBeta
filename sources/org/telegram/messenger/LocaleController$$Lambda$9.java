package org.telegram.messenger;

import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LocaleController$$Lambda$9 implements RequestDelegate {
    private final LocaleController arg$1;
    private final LocaleInfo arg$2;
    private final int arg$3;

    LocaleController$$Lambda$9(LocaleController localeController, LocaleInfo localeInfo, int i) {
        this.arg$1 = localeController;
        this.arg$2 = localeInfo;
        this.arg$3 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$applyRemoteLanguage$14$LocaleController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
