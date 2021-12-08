package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SessionsActivity$4$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ SessionsActivity$4$$ExternalSyntheticLambda1 INSTANCE = new SessionsActivity$4$$ExternalSyntheticLambda1();

    private /* synthetic */ SessionsActivity$4$$ExternalSyntheticLambda1() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(SessionsActivity$4$$ExternalSyntheticLambda0.INSTANCE);
    }
}
