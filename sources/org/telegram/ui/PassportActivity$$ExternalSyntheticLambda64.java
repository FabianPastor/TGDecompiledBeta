package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda64 implements RequestDelegate {
    public static final /* synthetic */ PassportActivity$$ExternalSyntheticLambda64 INSTANCE = new PassportActivity$$ExternalSyntheticLambda64();

    private /* synthetic */ PassportActivity$$ExternalSyntheticLambda64() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda40(tLObject));
    }
}
