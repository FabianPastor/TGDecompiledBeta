package org.telegram.messenger;

import android.net.Uri;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ Uri f$3;
    public final /* synthetic */ MessagesStorage.LongCallback f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda25(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, long j, Uri uri, MessagesStorage.LongCallback longCallback) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = uri;
        this.f$4 = longCallback;
    }

    public final void run() {
        this.f$0.m474x8d12017d(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
