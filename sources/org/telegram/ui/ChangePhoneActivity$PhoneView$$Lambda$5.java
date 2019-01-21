package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChangePhoneActivity.PhoneView;

final /* synthetic */ class ChangePhoneActivity$PhoneView$$Lambda$5 implements RequestDelegate {
    private final PhoneView arg$1;
    private final Bundle arg$2;
    private final TL_account_sendChangePhoneCode arg$3;

    ChangePhoneActivity$PhoneView$$Lambda$5(PhoneView phoneView, Bundle bundle, TL_account_sendChangePhoneCode tL_account_sendChangePhoneCode) {
        this.arg$1 = phoneView;
        this.arg$2 = bundle;
        this.arg$3 = tL_account_sendChangePhoneCode;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$7$ChangePhoneActivity$PhoneView(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
