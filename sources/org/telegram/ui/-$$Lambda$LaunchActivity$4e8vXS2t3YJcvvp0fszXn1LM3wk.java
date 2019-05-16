package org.telegram.ui;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$4e8vXS2t3YJcvvp0fszXn1LM3wk implements LocationActivityDelegate {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$4e8vXS2t3YJcvvp0fszXn1LM3wk(int[] iArr, long j) {
        this.f$0 = iArr;
        this.f$1 = j;
    }

    public final void didSelectLocation(MessageMedia messageMedia, int i) {
        SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(messageMedia, this.f$1, null, null, null);
    }
}
