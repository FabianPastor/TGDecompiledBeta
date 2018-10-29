package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PassportActivity$$Lambda$42 implements RequestDelegate {
    private final PassportActivity arg$1;
    private final String arg$2;
    private final PassportActivityDelegate arg$3;
    private final TL_account_sendVerifyPhoneCode arg$4;

    PassportActivity$$Lambda$42(PassportActivity passportActivity, String str, PassportActivityDelegate passportActivityDelegate, TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode) {
        this.arg$1 = passportActivity;
        this.arg$2 = str;
        this.arg$3 = passportActivityDelegate;
        this.arg$4 = tL_account_sendVerifyPhoneCode;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$startPhoneVerification$66$PassportActivity(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
