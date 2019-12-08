package org.telegram.ui;

import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditActivity$p1iHQUCNbkBH3bG3cN0lfw9rr7s implements LocationActivityDelegate {
    private final /* synthetic */ ChatEditActivity f$0;

    public /* synthetic */ -$$Lambda$ChatEditActivity$p1iHQUCNbkBH3bG3cN0lfw9rr7s(ChatEditActivity chatEditActivity) {
        this.f$0 = chatEditActivity;
    }

    public final void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2) {
        this.f$0.lambda$null$4$ChatEditActivity(messageMedia, i, z, i2);
    }
}
