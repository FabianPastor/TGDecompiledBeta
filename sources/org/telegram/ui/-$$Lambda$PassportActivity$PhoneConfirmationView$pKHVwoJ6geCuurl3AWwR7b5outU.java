package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$PhoneConfirmationView$pKHVwoJ6geCuurl3AWwR7b5outU implements Runnable {
    private final /* synthetic */ PhoneConfirmationView f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TL_account_verifyPhone f$2;

    public /* synthetic */ -$$Lambda$PassportActivity$PhoneConfirmationView$pKHVwoJ6geCuurl3AWwR7b5outU(PhoneConfirmationView phoneConfirmationView, TL_error tL_error, TL_account_verifyPhone tL_account_verifyPhone) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tL_error;
        this.f$2 = tL_account_verifyPhone;
    }

    public final void run() {
        this.f$0.lambda$null$6$PassportActivity$PhoneConfirmationView(this.f$1, this.f$2);
    }
}
