package org.telegram.ui.Components;

import java.util.HashMap;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class FragmentContextView$$ExternalSyntheticLambda3 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ LocationController.SharingLocationInfo f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ FragmentContextView$$ExternalSyntheticLambda3(LocationController.SharingLocationInfo sharingLocationInfo, long j) {
        this.f$0 = sharingLocationInfo;
        this.f$1 = j;
    }

    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(this.f$0.messageObject.currentAccount).sendMessage(messageMedia, this.f$1, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
    }
}
