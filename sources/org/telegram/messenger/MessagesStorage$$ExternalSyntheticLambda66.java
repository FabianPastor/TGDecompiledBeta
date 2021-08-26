package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$photos_Photos;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda66 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$photos_Photos f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda66(MessagesStorage messagesStorage, int i, TLRPC$photos_Photos tLRPC$photos_Photos, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = tLRPC$photos_Photos;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$putDialogPhotos$61(this.f$1, this.f$2, this.f$3);
    }
}
