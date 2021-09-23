package org.telegram.messenger;

import java.util.HashMap;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda141 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda141(MessagesStorage messagesStorage, HashMap hashMap, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = hashMap;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$putCachedPhoneBook$107(this.f$1, this.f$2);
    }
}
