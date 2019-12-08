package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$giNmHlRa_5kqtGWuY0622e5Jzzg implements IntCallback {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Runnable f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesController$giNmHlRa_5kqtGWuY0622e5Jzzg(MessagesController messagesController, Runnable runnable, long j, int i) {
        this.f$0 = messagesController;
        this.f$1 = runnable;
        this.f$2 = j;
        this.f$3 = i;
    }

    public final void run(int i) {
        this.f$0.lambda$ensureMessagesLoaded$281$MessagesController(this.f$1, this.f$2, this.f$3, i);
    }
}
