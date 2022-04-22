package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LocaleController$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ LocaleController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LocaleController$$ExternalSyntheticLambda11(LocaleController localeController, int i) {
        this.f$0 = localeController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadRemoteLanguages$7(this.f$1, tLObject, tLRPC$TL_error);
    }
}
