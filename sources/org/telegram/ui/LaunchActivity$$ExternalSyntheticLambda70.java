package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda70 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Bundle f$3;
    public final /* synthetic */ TLRPC$TL_account_sendConfirmPhoneCode f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda70(LaunchActivity launchActivity, AlertDialog alertDialog, String str, Bundle bundle, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = str;
        this.f$3 = bundle;
        this.f$4 = tLRPC$TL_account_sendConfirmPhoneCode;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$handleIntent$12(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
