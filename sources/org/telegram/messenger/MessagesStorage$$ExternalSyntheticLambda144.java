package org.telegram.messenger;

import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda144 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda144(MessagesStorage messagesStorage, MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = dialogFilter;
        this.f$2 = z;
        this.f$3 = z2;
    }

    public final void run() {
        this.f$0.lambda$saveDialogFilter$44(this.f$1, this.f$2, this.f$3);
    }
}
