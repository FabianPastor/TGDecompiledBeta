package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ ChangePhoneActivity.PhoneView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC$TL_account_sendChangePhoneCode f$2;

    public /* synthetic */ ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda7(ChangePhoneActivity.PhoneView phoneView, Bundle bundle, TLRPC$TL_account_sendChangePhoneCode tLRPC$TL_account_sendChangePhoneCode) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = tLRPC$TL_account_sendChangePhoneCode;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$7(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}