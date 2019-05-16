package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$PhoneConfirmationView$H7W-zFejxtN_G3EXdQMEm98R9oE implements RequestDelegate {
    private final /* synthetic */ PhoneConfirmationView f$0;
    private final /* synthetic */ TL_account_verifyPhone f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$PhoneConfirmationView$H7W-zFejxtN_G3EXdQMEm98R9oE(PhoneConfirmationView phoneConfirmationView, TL_account_verifyPhone tL_account_verifyPhone) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tL_account_verifyPhone;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onNextPressed$7$PassportActivity$PhoneConfirmationView(this.f$1, tLObject, tL_error);
    }
}
