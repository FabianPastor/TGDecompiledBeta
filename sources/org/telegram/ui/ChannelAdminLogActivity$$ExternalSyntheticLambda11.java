package org.telegram.ui;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter;
import org.telegram.ui.Components.AdminLogFilterAlert;

public final /* synthetic */ class ChannelAdminLogActivity$$ExternalSyntheticLambda11 implements AdminLogFilterAlert.AdminLogFilterAlertDelegate {
    public final /* synthetic */ ChannelAdminLogActivity f$0;

    public /* synthetic */ ChannelAdminLogActivity$$ExternalSyntheticLambda11(ChannelAdminLogActivity channelAdminLogActivity) {
        this.f$0 = channelAdminLogActivity;
    }

    public final void didSelectRights(TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, SparseArray sparseArray) {
        this.f$0.lambda$createView$4(tLRPC$TL_channelAdminLogEventsFilter, sparseArray);
    }
}
