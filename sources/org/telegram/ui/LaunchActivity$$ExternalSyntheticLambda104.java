package org.telegram.ui;

import org.telegram.ui.PaymentFormActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda104 implements PaymentFormActivity.PaymentFormCallback {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda104(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
        LaunchActivity.lambda$runLinkRequest$29(this.f$0, invoiceStatus);
    }
}
