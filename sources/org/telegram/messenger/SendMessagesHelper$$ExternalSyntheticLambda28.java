package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ SendMessagesHelper.ImportingHistory f$3;
    public final /* synthetic */ MessagesStorage.LongCallback f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda28(SendMessagesHelper sendMessagesHelper, HashMap hashMap, long j, SendMessagesHelper.ImportingHistory importingHistory, MessagesStorage.LongCallback longCallback) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = hashMap;
        this.f$2 = j;
        this.f$3 = importingHistory;
        this.f$4 = longCallback;
    }

    public final void run() {
        this.f$0.m465xa7d092bc(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
