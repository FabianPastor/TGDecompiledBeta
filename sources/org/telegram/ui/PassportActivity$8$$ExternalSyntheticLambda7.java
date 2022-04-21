package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda7(PassportActivity.AnonymousClass8 r1, boolean z) {
        this.f$0 = r1;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2787lambda$run$10$orgtelegramuiPassportActivity$8(this.f$1, tLObject, tL_error);
    }
}
