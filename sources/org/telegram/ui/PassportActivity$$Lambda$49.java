package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendVerifyPhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PassportActivity$$Lambda$49 implements Runnable {
    private final PassportActivity arg$1;
    private final TL_error arg$2;
    private final String arg$3;
    private final PassportActivityDelegate arg$4;
    private final TLObject arg$5;
    private final TL_account_sendVerifyPhoneCode arg$6;

    PassportActivity$$Lambda$49(PassportActivity passportActivity, TL_error tL_error, String str, PassportActivityDelegate passportActivityDelegate, TLObject tLObject, TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode) {
        this.arg$1 = passportActivity;
        this.arg$2 = tL_error;
        this.arg$3 = str;
        this.arg$4 = passportActivityDelegate;
        this.arg$5 = tLObject;
        this.arg$6 = tL_account_sendVerifyPhoneCode;
    }

    public void run() {
        this.arg$1.lambda$null$65$PassportActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
