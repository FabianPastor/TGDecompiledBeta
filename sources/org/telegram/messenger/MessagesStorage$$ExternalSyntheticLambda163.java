package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda163 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda163(MessagesStorage messagesStorage, TLRPC$Message tLRPC$Message, boolean z, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Message;
        this.f$2 = z;
        this.f$3 = arrayList;
        this.f$4 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$replaceMessageIfExists$167(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
