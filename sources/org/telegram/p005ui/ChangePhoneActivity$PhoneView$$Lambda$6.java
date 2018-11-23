package org.telegram.p005ui;

import android.os.Bundle;
import org.telegram.p005ui.ChangePhoneActivity.PhoneView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangePhoneActivity$PhoneView$$Lambda$6 */
final /* synthetic */ class ChangePhoneActivity$PhoneView$$Lambda$6 implements Runnable {
    private final PhoneView arg$1;
    private final TL_error arg$2;
    private final Bundle arg$3;
    private final TLObject arg$4;
    private final TL_account_sendChangePhoneCode arg$5;

    ChangePhoneActivity$PhoneView$$Lambda$6(PhoneView phoneView, TL_error tL_error, Bundle bundle, TLObject tLObject, TL_account_sendChangePhoneCode tL_account_sendChangePhoneCode) {
        this.arg$1 = phoneView;
        this.arg$2 = tL_error;
        this.arg$3 = bundle;
        this.arg$4 = tLObject;
        this.arg$5 = tL_account_sendChangePhoneCode;
    }

    public void run() {
        this.arg$1.lambda$null$6$ChangePhoneActivity$PhoneView(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
