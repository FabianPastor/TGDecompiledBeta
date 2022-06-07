package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda30 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ MessagesStorage.StringCallback f$5;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda30(SendMessagesHelper sendMessagesHelper, String str, String str2, String str3, ArrayList arrayList, MessagesStorage.StringCallback stringCallback) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = str3;
        this.f$4 = arrayList;
        this.f$5 = stringCallback;
    }

    public final void run() {
        this.f$0.lambda$prepareImportStickers$72(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
