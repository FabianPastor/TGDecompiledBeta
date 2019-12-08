package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$E5MYBH90F0ejaiZuWkG9pKYhZNI implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$E5MYBH90F0ejaiZuWkG9pKYhZNI INSTANCE = new -$$Lambda$ChatActivity$E5MYBH90F0ejaiZuWkG9pKYhZNI();

    private /* synthetic */ -$$Lambda$ChatActivity$E5MYBH90F0ejaiZuWkG9pKYhZNI() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$-FP-eF3isumyx06LDX_sd84QHYA(tLObject));
    }
}
