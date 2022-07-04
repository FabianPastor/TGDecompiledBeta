package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.photos_Photos f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda10(MessagesStorage messagesStorage, long j, TLRPC.photos_Photos photos_photos, ArrayList arrayList) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = photos_photos;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.m2243lambda$putDialogPhotos$70$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
