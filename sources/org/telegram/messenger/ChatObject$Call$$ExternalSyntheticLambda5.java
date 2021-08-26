package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda5(ChatObject.Call call, TLObject tLObject) {
        this.f$0 = call;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$reloadGroupCall$7(this.f$1);
    }
}
