package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$CUA4k7iWdg95RHzQNhBQbiiol64 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$CUA4k7iWdg95RHzQNhBQbiiol64 INSTANCE = new -$$Lambda$ChatActivity$CUA4k7iWdg95RHzQNhBQbiiol64();

    private /* synthetic */ -$$Lambda$ChatActivity$CUA4k7iWdg95RHzQNhBQbiiol64() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$oowjB9tzAPgOB3T98vUliMe-CPc(tLObject));
    }
}
