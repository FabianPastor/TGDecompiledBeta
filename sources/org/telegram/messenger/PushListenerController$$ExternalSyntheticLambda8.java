package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PushListenerController$$ExternalSyntheticLambda8 implements RequestDelegate {
    public static final /* synthetic */ PushListenerController$$ExternalSyntheticLambda8 INSTANCE = new PushListenerController$$ExternalSyntheticLambda8();

    private /* synthetic */ PushListenerController$$ExternalSyntheticLambda8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PushListenerController$$ExternalSyntheticLambda7(tLRPC$TL_error));
    }
}
