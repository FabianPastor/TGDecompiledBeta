package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda63 implements RequestDelegate {
    public final /* synthetic */ PassportActivity f$0;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda63(PassportActivity passportActivity) {
        this.f$0 = passportActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPasswordInfo$4(tLObject, tLRPC$TL_error);
    }
}