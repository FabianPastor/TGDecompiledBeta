package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ LocaleController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LocaleController$$ExternalSyntheticLambda2(LocaleController localeController, int i) {
        this.f$0 = localeController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m81xCLASSNAMEd4(this.f$1, tLObject, tL_error);
    }
}
