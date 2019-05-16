package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PaymentFormActivity$v6Le3V4A5uKjdxXgwOCsgIj66wA implements Runnable {
    private final /* synthetic */ PaymentFormActivity f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ TL_account_updatePasswordSettings f$4;

    public /* synthetic */ -$$Lambda$PaymentFormActivity$v6Le3V4A5uKjdxXgwOCsgIj66wA(PaymentFormActivity paymentFormActivity, boolean z, String str, String str2, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.f$0 = paymentFormActivity;
        this.f$1 = z;
        this.f$2 = str;
        this.f$3 = str2;
        this.f$4 = tL_account_updatePasswordSettings;
    }

    public final void run() {
        this.f$0.lambda$sendSavePassword$33$PaymentFormActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
