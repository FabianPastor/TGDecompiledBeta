package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10 implements RequestDelegate {
    public static final /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10 INSTANCE = new PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10();

    private /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PassportActivity.PhoneConfirmationView.lambda$onBackPressed$9(tLObject, tLRPC$TL_error);
    }
}
