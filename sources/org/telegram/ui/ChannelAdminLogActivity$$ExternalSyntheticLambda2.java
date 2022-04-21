package org.telegram.ui;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AdminLogFilterAlert;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda2 implements AdminLogFilterAlert.AdminLogFilterAlertDelegate {
    public final /* synthetic */ ChannelAdminLogActivity f$0;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda2(ChannelAdminLogActivity channelAdminLogActivity) {
        this.f$0 = channelAdminLogActivity;
    }

    public final void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, LongSparseArray longSparseArray) {
        this.f$0.m1582lambda$createView$4$orgtelegramuiChannelAdminLogActivity(tL_channelAdminLogEventsFilter, longSparseArray);
    }
}
