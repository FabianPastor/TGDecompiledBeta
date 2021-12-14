package org.telegram.messenger;

import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda3(ChatObject.Call call, TLObject tLObject) {
        this.f$0 = call;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadGroupCall$9(this.f$1);
    }
}
