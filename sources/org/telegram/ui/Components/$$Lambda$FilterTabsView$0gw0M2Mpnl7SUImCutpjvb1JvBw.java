package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.Components.-$$Lambda$FilterTabsView$0gw0M2Mpnl7SUImCutpjvb1JvBw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FilterTabsView$0gw0M2Mpnl7SUImCutpjvb1JvBw implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FilterTabsView$0gw0M2Mpnl7SUImCutpjvb1JvBw INSTANCE = new $$Lambda$FilterTabsView$0gw0M2Mpnl7SUImCutpjvb1JvBw();

    private /* synthetic */ $$Lambda$FilterTabsView$0gw0M2Mpnl7SUImCutpjvb1JvBw() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FilterTabsView.lambda$setIsEditing$2(tLObject, tLRPC$TL_error);
    }
}
