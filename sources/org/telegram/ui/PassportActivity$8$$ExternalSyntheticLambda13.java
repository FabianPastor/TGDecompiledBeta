package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda13(PassportActivity.AnonymousClass8 r1, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2778lambda$generateNewSecret$4$orgtelegramuiPassportActivity$8(this.f$1, this.f$2);
    }
}
