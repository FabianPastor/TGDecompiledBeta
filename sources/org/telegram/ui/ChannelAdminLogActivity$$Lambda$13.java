package org.telegram.ui;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.ui.Components.AdminLogFilterAlert.AdminLogFilterAlertDelegate;

final /* synthetic */ class ChannelAdminLogActivity$$Lambda$13 implements AdminLogFilterAlertDelegate {
    private final ChannelAdminLogActivity arg$1;

    ChannelAdminLogActivity$$Lambda$13(ChannelAdminLogActivity channelAdminLogActivity) {
        this.arg$1 = channelAdminLogActivity;
    }

    public void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray sparseArray) {
        this.arg$1.lambda$null$4$ChannelAdminLogActivity(tL_channelAdminLogEventsFilter, sparseArray);
    }
}
