package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PassportActivity$$Lambda$0 implements RequestDelegate {
    static final RequestDelegate $instance = new PassportActivity$$Lambda$0();

    private PassportActivity$$Lambda$0() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new PassportActivity$$Lambda$74(tLObject));
    }
}
