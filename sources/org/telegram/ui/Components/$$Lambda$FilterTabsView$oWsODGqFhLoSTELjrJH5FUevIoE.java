package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.Components.-$$Lambda$FilterTabsView$oWsODGqFhLoSTELjrJH5FUevIoE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FilterTabsView$oWsODGqFhLoSTELjrJH5FUevIoE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FilterTabsView$oWsODGqFhLoSTELjrJH5FUevIoE INSTANCE = new $$Lambda$FilterTabsView$oWsODGqFhLoSTELjrJH5FUevIoE();

    private /* synthetic */ $$Lambda$FilterTabsView$oWsODGqFhLoSTELjrJH5FUevIoE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FilterTabsView.lambda$setIsEditing$2(tLObject, tLRPC$TL_error);
    }
}
