package org.telegram.messenger;

import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ LocaleController f$0;
    public final /* synthetic */ LocaleController.LocaleInfo f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ LocaleController$$ExternalSyntheticLambda4(LocaleController localeController, LocaleController.LocaleInfo localeInfo, int i) {
        this.f$0 = localeController;
        this.f$1 = localeInfo;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m76x310ffce7(this.f$1, this.f$2, tLObject, tL_error);
    }
}
