package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda11(PassportActivity.AnonymousClass8 r1, TLRPC.TL_error tL_error) {
        this.f$0 = r1;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m4106lambda$generateNewSecret$6$orgtelegramuiPassportActivity$8(this.f$1);
    }
}
