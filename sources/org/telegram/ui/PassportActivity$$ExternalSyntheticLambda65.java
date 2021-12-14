package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda65 implements RequestDelegate {
    public final /* synthetic */ PassportActivity f$0;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda65(PassportActivity passportActivity) {
        this.f$0 = passportActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createRequestInterface$15(tLObject, tLRPC$TL_error);
    }
}
