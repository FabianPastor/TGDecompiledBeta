package org.telegram.ui;

import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class ChatEditActivity$$ExternalSyntheticLambda28 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ ChatEditActivity f$0;

    public /* synthetic */ ChatEditActivity$$ExternalSyntheticLambda28(ChatEditActivity chatEditActivity) {
        this.f$0 = chatEditActivity;
    }

    public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        this.f$0.lambda$createView$8(tLRPC$MessageMedia, i, z, i2);
    }
}
