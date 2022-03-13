package org.telegram.ui.Components;

import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda85 implements RequestDelegate {
    public final /* synthetic */ AccountInstance f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda85(AccountInstance accountInstance) {
        this.f$0 = accountInstance;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.lambda$showBlockReportSpamReplyAlert$8(this.f$0, tLObject, tLRPC$TL_error);
    }
}
