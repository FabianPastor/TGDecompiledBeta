package org.telegram.messenger;

import android.net.Uri;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda31 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ Uri f$3;
    public final /* synthetic */ MessagesStorage.IntCallback f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda31(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, long j, Uri uri, MessagesStorage.IntCallback intCallback) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = uri;
        this.f$4 = intCallback;
    }

    public final void run() {
        this.f$0.lambda$prepareImportHistory$69(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
