package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_assignPlayMarketTransaction;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_payments_assignPlayMarketTransaction f$2;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda8(BaseFragment baseFragment, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction) {
        this.f$0 = baseFragment;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_payments_assignPlayMarketTransaction;
    }

    public final void run() {
        AlertsCreator.processError(this.f$0.getCurrentAccount(), this.f$1, this.f$0, this.f$2, new Object[0]);
    }
}
