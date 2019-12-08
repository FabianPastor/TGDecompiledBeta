package org.telegram.messenger;

import org.telegram.messenger.TonController.BytesCallback;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AndroidUtilities$jQk6Chp51iovYoXvar_WJJjUc2DA implements RequestDelegate {
    private final /* synthetic */ BytesCallback f$0;

    public /* synthetic */ -$$Lambda$AndroidUtilities$jQk6Chp51iovYoXvar_WJJjUc2DA(BytesCallback bytesCallback) {
        this.f$0 = bytesCallback;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.lambda$getTonWalletSalt$7(this.f$0, tLObject, tL_error);
    }
}
