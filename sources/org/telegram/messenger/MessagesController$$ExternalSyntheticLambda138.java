package org.telegram.messenger;

import androidx.core.util.Consumer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda138 implements RequestDelegate {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda138(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$ExternalSyntheticLambda125(tLObject, tL_error, this.f$0));
    }
}
