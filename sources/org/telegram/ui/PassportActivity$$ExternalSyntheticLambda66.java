package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda66 implements RequestDelegate {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ PassportActivity.PassportActivityDelegate f$2;
    public final /* synthetic */ TLRPC$TL_account_sendVerifyPhoneCode f$3;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda66(PassportActivity passportActivity, String str, PassportActivity.PassportActivityDelegate passportActivityDelegate, TLRPC$TL_account_sendVerifyPhoneCode tLRPC$TL_account_sendVerifyPhoneCode) {
        this.f$0 = passportActivity;
        this.f$1 = str;
        this.f$2 = passportActivityDelegate;
        this.f$3 = tLRPC$TL_account_sendVerifyPhoneCode;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$startPhoneVerification$66(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
