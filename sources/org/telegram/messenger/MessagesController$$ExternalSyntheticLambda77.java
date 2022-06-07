package org.telegram.messenger;

import androidx.core.util.Consumer;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Consumer f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda77(MessagesController messagesController, long j, Consumer consumer, int i) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = consumer;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$getNextReactionMention$5(this.f$1, this.f$2, this.f$3);
    }
}
