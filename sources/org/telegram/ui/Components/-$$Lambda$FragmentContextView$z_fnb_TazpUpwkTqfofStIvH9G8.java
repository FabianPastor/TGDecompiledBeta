package org.telegram.ui.Components;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FragmentContextView$z_fnb_TazpUpwkTqfofStIvH9G8 implements LocationActivityDelegate {
    private final /* synthetic */ SharingLocationInfo f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$FragmentContextView$z_fnb_TazpUpwkTqfofStIvH9G8(SharingLocationInfo sharingLocationInfo, long j) {
        this.f$0 = sharingLocationInfo;
        this.f$1 = j;
    }

    public final void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(this.f$0.messageObject.currentAccount).sendMessage(messageMedia, this.f$1, null, null, null, z, i2);
    }
}
