package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$2 implements RequestDelegate {
    private final PhoneConfirmationView arg$1;
    private final Bundle arg$2;
    private final TL_auth_resendCode arg$3;

    PassportActivity$PhoneConfirmationView$$Lambda$2(PhoneConfirmationView phoneConfirmationView, Bundle bundle, TL_auth_resendCode tL_auth_resendCode) {
        this.arg$1 = phoneConfirmationView;
        this.arg$2 = bundle;
        this.arg$3 = tL_auth_resendCode;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$resendCode$4$PassportActivity$PhoneConfirmationView(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
