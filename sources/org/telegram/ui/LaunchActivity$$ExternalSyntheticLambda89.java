package org.telegram.ui;

import java.util.HashMap;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda89 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda89(int[] iArr, long j) {
        this.f$0 = iArr;
        this.f$1 = j;
    }

    public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(this.f$0[0]).sendMessage(tLRPC$MessageMedia, this.f$1, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
    }
}
