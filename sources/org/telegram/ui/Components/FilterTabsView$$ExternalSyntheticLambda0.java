package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FilterTabsView$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ FilterTabsView$$ExternalSyntheticLambda0 INSTANCE = new FilterTabsView$$ExternalSyntheticLambda0();

    private /* synthetic */ FilterTabsView$$ExternalSyntheticLambda0() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FilterTabsView.lambda$setIsEditing$2(tLObject, tL_error);
    }
}
