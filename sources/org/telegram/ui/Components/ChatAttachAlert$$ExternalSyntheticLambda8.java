package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda8 implements ChatAttachAlertLocationLayout.LocationActivityDelegate {
    public final /* synthetic */ ChatAttachAlert f$0;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda8(ChatAttachAlert chatAttachAlert) {
        this.f$0 = chatAttachAlert;
    }

    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        this.f$0.m2103lambda$new$4$orgtelegramuiComponentsChatAttachAlert(messageMedia, i, z, i2);
    }
}
