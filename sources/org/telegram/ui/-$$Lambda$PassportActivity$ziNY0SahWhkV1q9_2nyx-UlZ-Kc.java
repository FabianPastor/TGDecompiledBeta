package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$ziNY0SahWhkV1q9_2nyx-UlZ-Kc implements RequestDelegate {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ PassportActivityDelegate f$2;
    private final /* synthetic */ TL_account_sendVerifyPhoneCode f$3;

    public /* synthetic */ -$$Lambda$PassportActivity$ziNY0SahWhkV1q9_2nyx-UlZ-Kc(PassportActivity passportActivity, String str, PassportActivityDelegate passportActivityDelegate, TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode) {
        this.f$0 = passportActivity;
        this.f$1 = str;
        this.f$2 = passportActivityDelegate;
        this.f$3 = tL_account_sendVerifyPhoneCode;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$startPhoneVerification$67$PassportActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
