package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda53 implements Runnable {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ PassportActivity.PassportActivityDelegate f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ TLRPC.TL_account_sendVerifyPhoneCode f$5;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda53(PassportActivity passportActivity, TLRPC.TL_error tL_error, String str, PassportActivity.PassportActivityDelegate passportActivityDelegate, TLObject tLObject, TLRPC.TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode) {
        this.f$0 = passportActivity;
        this.f$1 = tL_error;
        this.f$2 = str;
        this.f$3 = passportActivityDelegate;
        this.f$4 = tLObject;
        this.f$5 = tL_account_sendVerifyPhoneCode;
    }

    public final void run() {
        this.f$0.m2765x6a40fb7c(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
