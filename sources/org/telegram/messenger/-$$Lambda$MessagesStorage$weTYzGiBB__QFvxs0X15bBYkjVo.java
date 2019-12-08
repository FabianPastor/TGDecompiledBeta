package org.telegram.messenger;

import java.util.HashMap;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$weTYzGiBB__QFvxs0X15bBYkjVo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$weTYzGiBB__QFvxs0X15bBYkjVo(MessagesStorage messagesStorage, HashMap hashMap, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = hashMap;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$putCachedPhoneBook$88$MessagesStorage(this.f$1, this.f$2);
    }
}
