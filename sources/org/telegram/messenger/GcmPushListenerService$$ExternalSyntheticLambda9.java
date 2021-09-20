package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9 INSTANCE = new GcmPushListenerService$$ExternalSyntheticLambda9();

    private /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda8(tLRPC$TL_error));
    }
}
