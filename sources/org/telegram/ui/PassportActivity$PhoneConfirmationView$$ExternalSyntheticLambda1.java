package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda1 INSTANCE = new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda1();

    private /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda1() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PassportActivity.PhoneConfirmationView.lambda$onBackPressed$9(tLObject, tL_error);
    }
}
