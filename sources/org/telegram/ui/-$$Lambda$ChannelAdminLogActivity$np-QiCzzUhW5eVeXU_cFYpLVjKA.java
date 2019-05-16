package org.telegram.ui;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.ui.Components.AdminLogFilterAlert.AdminLogFilterAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChannelAdminLogActivity$np-QiCzzUhW5eVeXU_cFYpLVjKA implements AdminLogFilterAlertDelegate {
    private final /* synthetic */ ChannelAdminLogActivity f$0;

    public /* synthetic */ -$$Lambda$ChannelAdminLogActivity$np-QiCzzUhW5eVeXU_cFYpLVjKA(ChannelAdminLogActivity channelAdminLogActivity) {
        this.f$0 = channelAdminLogActivity;
    }

    public final void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray sparseArray) {
        this.f$0.lambda$null$4$ChannelAdminLogActivity(tL_channelAdminLogEventsFilter, sparseArray);
    }
}
