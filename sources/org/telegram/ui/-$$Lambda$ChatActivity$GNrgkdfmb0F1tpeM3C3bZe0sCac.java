package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$GNrgkdfmb0F1tpeM3C3bZe0sCac implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$GNrgkdfmb0F1tpeM3C3bZe0sCac INSTANCE = new -$$Lambda$ChatActivity$GNrgkdfmb0F1tpeM3C3bZe0sCac();

    private /* synthetic */ -$$Lambda$ChatActivity$GNrgkdfmb0F1tpeM3C3bZe0sCac() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$4MFHImFw3v7oLavy_HZ2uZhqY2U(tLObject));
    }
}
