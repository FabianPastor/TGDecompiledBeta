package org.telegram.messenger;

import androidx.core.util.Consumer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda221 implements RequestDelegate {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda221(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$ExternalSyntheticLambda209(tLObject, tLRPC$TL_error, this.f$0));
    }
}
