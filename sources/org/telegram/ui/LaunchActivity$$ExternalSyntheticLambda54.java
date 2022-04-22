package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ Bundle f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ TLRPC$TL_account_sendConfirmPhoneCode f$6;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda54(LaunchActivity launchActivity, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, String str, Bundle bundle, TLObject tLObject, TLRPC$TL_account_sendConfirmPhoneCode tLRPC$TL_account_sendConfirmPhoneCode) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = str;
        this.f$4 = bundle;
        this.f$5 = tLObject;
        this.f$6 = tLRPC$TL_account_sendConfirmPhoneCode;
    }

    public final void run() {
        this.f$0.lambda$handleIntent$10(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
