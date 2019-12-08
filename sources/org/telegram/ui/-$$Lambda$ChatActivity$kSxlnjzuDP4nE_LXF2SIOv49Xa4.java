package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$kSxlnjzuDP4nE_LXF2SIOv49Xa4 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$kSxlnjzuDP4nE_LXF2SIOv49Xa4 INSTANCE = new -$$Lambda$ChatActivity$kSxlnjzuDP4nE_LXF2SIOv49Xa4();

    private /* synthetic */ -$$Lambda$ChatActivity$kSxlnjzuDP4nE_LXF2SIOv49Xa4() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$bAYKwxCWxHsAuJrW-RQww75L8I4(tLObject));
    }
}
