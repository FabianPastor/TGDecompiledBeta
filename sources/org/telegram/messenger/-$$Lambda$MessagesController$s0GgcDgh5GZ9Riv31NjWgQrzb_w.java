package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$s0GgcDgh5GZ9Riv31NjWgQrzb_w implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Updates f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$MessagesController$s0GgcDgh5GZ9Riv31NjWgQrzb_w(MessagesController messagesController, boolean z, Updates updates, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = updates;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$processUpdates$233$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}