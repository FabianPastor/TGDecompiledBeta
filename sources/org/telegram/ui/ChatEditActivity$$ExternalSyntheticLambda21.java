package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda21 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ ChatEditActivity f$0;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda21(ChatEditActivity chatEditActivity) {
        this.f$0 = chatEditActivity;
    }

    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        this.f$0.m1885lambda$createView$8$orgtelegramuiChatEditActivity(messageMedia, i, z, i2);
    }
}
