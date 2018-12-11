package org.telegram.p005ui;

import android.os.Bundle;
import org.telegram.p005ui.CancelAccountDeletionActivity.PhoneView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.CancelAccountDeletionActivity$PhoneView$$Lambda$1 */
final /* synthetic */ class CancelAccountDeletionActivity$PhoneView$$Lambda$1 implements Runnable {
    private final PhoneView arg$1;
    private final TL_error arg$2;
    private final Bundle arg$3;
    private final TLObject arg$4;
    private final TL_account_sendConfirmPhoneCode arg$5;

    CancelAccountDeletionActivity$PhoneView$$Lambda$1(PhoneView phoneView, TL_error tL_error, Bundle bundle, TLObject tLObject, TL_account_sendConfirmPhoneCode tL_account_sendConfirmPhoneCode) {
        this.arg$1 = phoneView;
        this.arg$2 = tL_error;
        this.arg$3 = bundle;
        this.arg$4 = tLObject;
        this.arg$5 = tL_account_sendConfirmPhoneCode;
    }

    public void run() {
        this.arg$1.lambda$null$0$CancelAccountDeletionActivity$PhoneView(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
