package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$n2GPnAOG5aN55gasvKHgSzWbYCo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$n2GPnAOG5aN55gasvKHgSzWbYCo(MessagesStorage messagesStorage, String str, TLObject tLObject, int i, String str2) {
        this.f$0 = messagesStorage;
        this.f$1 = str;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = str2;
    }

    public final void run() {
        this.f$0.lambda$putSentFile$99$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
