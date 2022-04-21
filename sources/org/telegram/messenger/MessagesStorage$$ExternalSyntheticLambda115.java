package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda115 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda115(MessagesStorage messagesStorage) {
        this.f$0 = messagesStorage;
    }

    public final void run() {
        this.f$0.saveDialogFiltersOrderInternal();
    }
}
