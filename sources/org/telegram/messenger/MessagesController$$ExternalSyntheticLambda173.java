package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$photos_Photos;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda173 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$photos_Photos f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ ArrayList f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda173(MessagesController messagesController, TLRPC$photos_Photos tLRPC$photos_Photos, boolean z, int i, int i2, int i3, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$photos_Photos;
        this.f$2 = z;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = i3;
        this.f$6 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processLoadedUserPhotos$84(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
