package org.telegram.messenger;

import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda142 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda142(MessagesStorage messagesStorage, MessagesController.DialogFilter dialogFilter) {
        this.f$0 = messagesStorage;
        this.f$1 = dialogFilter;
    }

    public final void run() {
        this.f$0.lambda$deleteDialogFilter$41(this.f$1);
    }
}
