package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChangePhoneActivity.PhoneView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangePhoneActivity$PhoneView$osRwjZiwNNc4O0LSbhXJvCtt8fY implements RequestDelegate {
    private final /* synthetic */ PhoneView f$0;
    private final /* synthetic */ Bundle f$1;
    private final /* synthetic */ TL_account_sendChangePhoneCode f$2;

    public /* synthetic */ -$$Lambda$ChangePhoneActivity$PhoneView$osRwjZiwNNc4O0LSbhXJvCtt8fY(PhoneView phoneView, Bundle bundle, TL_account_sendChangePhoneCode tL_account_sendChangePhoneCode) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = tL_account_sendChangePhoneCode;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$7$ChangePhoneActivity$PhoneView(this.f$1, this.f$2, tLObject, tL_error);
    }
}
