package org.telegram.messenger;

import org.telegram.messenger.ChatObject;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda0(ChatObject.Call call) {
        this.f$0 = call;
    }

    public final void run() {
        this.f$0.checkQueue();
    }
}