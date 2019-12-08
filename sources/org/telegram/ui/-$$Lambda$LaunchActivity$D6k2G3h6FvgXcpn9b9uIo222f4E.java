package org.telegram.ui;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$D6k2G3h6FvgXcpn9b9uIo222f4E implements LocationActivityDelegate {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$D6k2G3h6FvgXcpn9b9uIo222f4E(int[] iArr, long j) {
        this.f$0 = iArr;
        this.f$1 = j;
    }

    public final void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(messageMedia, this.f$1, null, null, null, z, i2);
    }
}
