package org.telegram.ui.Components;

import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

final /* synthetic */ class FragmentContextView$$Lambda$4 implements LocationActivityDelegate {
    private final SharingLocationInfo arg$1;
    private final long arg$2;

    FragmentContextView$$Lambda$4(SharingLocationInfo sharingLocationInfo, long j) {
        this.arg$1 = sharingLocationInfo;
        this.arg$2 = j;
    }

    public void didSelectLocation(MessageMedia messageMedia, int i) {
        SendMessagesHelper.getInstance(this.arg$1.messageObject.currentAccount).sendMessage(messageMedia, this.arg$2, null, null, null);
    }
}
