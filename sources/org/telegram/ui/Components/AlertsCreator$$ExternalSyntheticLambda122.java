package org.telegram.ui.Components;

import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda122 implements RequestDelegate {
    public final /* synthetic */ AccountInstance f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda122(AccountInstance accountInstance) {
        this.f$0 = accountInstance;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$showBlockReportSpamReplyAlert$11(this.f$0, tLObject, tL_error);
    }
}
