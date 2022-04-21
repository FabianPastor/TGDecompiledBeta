package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$20$1$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass20.AnonymousClass1 f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_secureRequiredType f$2;
    public final /* synthetic */ PassportActivity.PassportActivityDelegate f$3;
    public final /* synthetic */ PassportActivity.ErrorRunnable f$4;

    public /* synthetic */ PassportActivity$20$1$$ExternalSyntheticLambda3(PassportActivity.AnonymousClass20.AnonymousClass1 r1, String str, TLRPC.TL_secureRequiredType tL_secureRequiredType, PassportActivity.PassportActivityDelegate passportActivityDelegate, PassportActivity.ErrorRunnable errorRunnable) {
        this.f$0 = r1;
        this.f$1 = str;
        this.f$2 = tL_secureRequiredType;
        this.f$3 = passportActivityDelegate;
        this.f$4 = errorRunnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2769lambda$run$2$orgtelegramuiPassportActivity$20$1(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
