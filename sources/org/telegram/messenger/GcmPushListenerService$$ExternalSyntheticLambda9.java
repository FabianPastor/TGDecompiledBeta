package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9 INSTANCE = new GcmPushListenerService$$ExternalSyntheticLambda9();

    private /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$ExternalSyntheticLambda8(tL_error));
    }
}
