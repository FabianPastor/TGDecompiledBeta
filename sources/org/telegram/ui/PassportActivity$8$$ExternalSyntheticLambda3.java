package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_passwordSettings;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$8$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ PassportActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC$TL_account_passwordSettings f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ byte[] f$3;

    public /* synthetic */ PassportActivity$8$$ExternalSyntheticLambda3(PassportActivity.AnonymousClass8 r1, TLRPC$TL_account_passwordSettings tLRPC$TL_account_passwordSettings, boolean z, byte[] bArr) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_account_passwordSettings;
        this.f$2 = z;
        this.f$3 = bArr;
    }

    public final void run() {
        this.f$0.lambda$run$14(this.f$1, this.f$2, this.f$3);
    }
}
