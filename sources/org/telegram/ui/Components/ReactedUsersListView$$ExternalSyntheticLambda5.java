package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ReactedUsersListView f$0;

    public /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda5(ReactedUsersListView reactedUsersListView) {
        this.f$0 = reactedUsersListView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$load$6(tLObject, tLRPC$TL_error);
    }
}
