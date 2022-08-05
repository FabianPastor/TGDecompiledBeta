package org.telegram.messenger;

import android.net.Uri;
import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda78 implements MessagesStorage.LongCallback {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ Uri f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ MessagesStorage.LongCallback f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda78(SendMessagesHelper sendMessagesHelper, Uri uri, ArrayList arrayList, MessagesStorage.LongCallback longCallback) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = uri;
        this.f$2 = arrayList;
        this.f$3 = longCallback;
    }

    public final void run(long j) {
        this.f$0.lambda$prepareImportHistory$64(this.f$1, this.f$2, this.f$3, j);
    }
}
