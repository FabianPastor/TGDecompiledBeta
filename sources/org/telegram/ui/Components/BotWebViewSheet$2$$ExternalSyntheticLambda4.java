package org.telegram.ui.Components;

import org.telegram.ui.Components.BotWebViewSheet;
import org.telegram.ui.PaymentFormActivity;

public final /* synthetic */ class BotWebViewSheet$2$$ExternalSyntheticLambda4 implements PaymentFormActivity.PaymentFormCallback {
    public final /* synthetic */ BotWebViewSheet.AnonymousClass2 f$0;
    public final /* synthetic */ OverlayActionBarLayoutDialog f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ BotWebViewSheet$2$$ExternalSyntheticLambda4(BotWebViewSheet.AnonymousClass2 r1, OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str) {
        this.f$0 = r1;
        this.f$1 = overlayActionBarLayoutDialog;
        this.f$2 = str;
    }

    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
        this.f$0.lambda$onWebAppOpenInvoice$3(this.f$1, this.f$2, invoiceStatus);
    }
}
