package org.telegram.messenger;

import androidx.core.util.Consumer;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda328 implements Runnable {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda328(Consumer consumer, int i) {
        this.f$0 = consumer;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.accept(Integer.valueOf(this.f$1));
    }
}
