package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.CancelAccountDeletionActivity.PhoneView;

final /* synthetic */ class CancelAccountDeletionActivity$PhoneView$$Lambda$0 implements RequestDelegate {
    private final PhoneView arg$1;
    private final Bundle arg$2;
    private final TL_account_sendConfirmPhoneCode arg$3;

    CancelAccountDeletionActivity$PhoneView$$Lambda$0(PhoneView phoneView, Bundle bundle, TL_account_sendConfirmPhoneCode tL_account_sendConfirmPhoneCode) {
        this.arg$1 = phoneView;
        this.arg$2 = bundle;
        this.arg$3 = tL_account_sendConfirmPhoneCode;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$1$CancelAccountDeletionActivity$PhoneView(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
