package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda61 implements RequestDelegate {
    public final /* synthetic */ PassportActivity f$0;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda61(PassportActivity passportActivity) {
        this.f$0 = passportActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3414lambda$loadPasswordInfo$4$orgtelegramuiPassportActivity(tLObject, tL_error);
    }
}
