package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$photos_Photos;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda181 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$photos_Photos f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda181(MessagesStorage messagesStorage, TLRPC$photos_Photos tLRPC$photos_Photos, ArrayList arrayList, long j, int i, int i2, int i3) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$photos_Photos;
        this.f$2 = arrayList;
        this.f$3 = j;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = i3;
    }

    public final void run() {
        this.f$0.lambda$getDialogPhotos$64(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
