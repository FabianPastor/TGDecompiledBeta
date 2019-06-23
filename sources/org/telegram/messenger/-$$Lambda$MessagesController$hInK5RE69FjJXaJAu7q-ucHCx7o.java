package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hInK5RE69FjJXaJAu7q-ucHCx7o implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$hInK5RE69FjJXaJAu7q-ucHCx7o INSTANCE = new -$$Lambda$MessagesController$hInK5RE69FjJXaJAu7q-ucHCx7o();

    private /* synthetic */ -$$Lambda$MessagesController$hInK5RE69FjJXaJAu7q-ucHCx7o() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$hidePeerSettingsBar$25(tLObject, tL_error);
    }
}
