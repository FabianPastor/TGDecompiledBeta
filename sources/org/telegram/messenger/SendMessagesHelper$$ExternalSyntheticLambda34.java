package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda34 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ SendMessagesHelper.ImportingStickers f$1;
    public final /* synthetic */ HashMap f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ MessagesStorage.StringCallback f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda34(SendMessagesHelper sendMessagesHelper, SendMessagesHelper.ImportingStickers importingStickers, HashMap hashMap, String str, MessagesStorage.StringCallback stringCallback) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = importingStickers;
        this.f$2 = hashMap;
        this.f$3 = str;
        this.f$4 = stringCallback;
    }

    public final void run() {
        this.f$0.m467x14e80794(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
