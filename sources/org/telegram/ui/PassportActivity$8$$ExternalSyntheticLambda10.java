package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC.TL_account_passwordSettings f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ byte[] f$3;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda10(PassportActivity.AnonymousClass8 r1, TLRPC.TL_account_passwordSettings tL_account_passwordSettings, boolean z, byte[] bArr) {
        this.f$0 = r1;
        this.f$1 = tL_account_passwordSettings;
        this.f$2 = z;
        this.f$3 = bArr;
    }

    public final void run() {
        this.f$0.m2791lambda$run$14$orgtelegramuiPassportActivity$8(this.f$1, this.f$2, this.f$3);
    }
}
