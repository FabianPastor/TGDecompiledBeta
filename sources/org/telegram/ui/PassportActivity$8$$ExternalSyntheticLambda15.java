package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda15(PassportActivity.AnonymousClass8 r1, TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m4120lambda$run$9$orgtelegramuiPassportActivity$8(this.f$1, this.f$2, this.f$3);
    }
}
