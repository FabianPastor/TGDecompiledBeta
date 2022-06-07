package org.telegram.ui.Components;

import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.PaymentFormActivity;

public final /* synthetic */ class ChatAttachAlert$1$$ExternalSyntheticLambda3 implements PaymentFormActivity.PaymentFormCallback {
    public final /* synthetic */ OverlayActionBarLayoutDialog f$0;
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ ChatAttachAlert$1$$ExternalSyntheticLambda3(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str) {
        this.f$0 = overlayActionBarLayoutDialog;
        this.f$1 = chatAttachAlertBotWebViewLayout;
        this.f$2 = str;
    }

    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
        ChatAttachAlert.AnonymousClass1.lambda$onWebAppOpenInvoice$2(this.f$0, this.f$1, this.f$2, invoiceStatus);
    }
}
