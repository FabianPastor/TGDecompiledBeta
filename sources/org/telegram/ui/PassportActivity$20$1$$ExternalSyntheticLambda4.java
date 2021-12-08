package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$20$1$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass20.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_secureValue f$1;

    public /* synthetic */ PassportActivity$20$1$$ExternalSyntheticLambda4(PassportActivity.AnonymousClass20.AnonymousClass1 r1, TLRPC.TL_secureValue tL_secureValue) {
        this.f$0 = r1;
        this.f$1 = tL_secureValue;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3428lambda$run$4$orgtelegramuiPassportActivity$20$1(this.f$1, tLObject, tL_error);
    }
}
