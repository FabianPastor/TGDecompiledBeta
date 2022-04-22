package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda68 implements RequestDelegate {
    public static final /* synthetic */ PassportActivity$$ExternalSyntheticLambda68 INSTANCE = new PassportActivity$$ExternalSyntheticLambda68();

    private /* synthetic */ PassportActivity$$ExternalSyntheticLambda68() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$ExternalSyntheticLambda46(tLObject));
    }
}
