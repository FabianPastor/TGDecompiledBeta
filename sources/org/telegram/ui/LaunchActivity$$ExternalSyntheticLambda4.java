package org.telegram.ui;

import java.util.HashMap;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda4 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda4(int[] iArr, long j) {
        this.f$0 = iArr;
        this.f$1 = j;
    }

    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(messageMedia, this.f$1, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
    }
}
