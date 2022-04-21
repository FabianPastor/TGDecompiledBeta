package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda5(PassportActivity.AnonymousClass8 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2786lambda$resetSecret$3$orgtelegramuiPassportActivity$8(tLObject, tL_error);
    }
}
