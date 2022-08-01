package org.telegram.ui;

import org.telegram.ui.PaymentFormActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda105 implements PaymentFormActivity.PaymentFormCallback {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda105(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
        LaunchActivity.lambda$runLinkRequest$30(this.f$0, invoiceStatus);
    }
}
