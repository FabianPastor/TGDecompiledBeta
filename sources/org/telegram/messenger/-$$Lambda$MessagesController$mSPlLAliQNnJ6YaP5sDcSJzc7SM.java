package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$mSPlLAliQNnJ6YaP5sDcSJzc7SM implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$MessagesController$mSPlLAliQNnJ6YaP5sDcSJzc7SM(MessagesController messagesController, int i, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$generateJoinMessage$227$MessagesController(this.f$1, this.f$2);
    }
}
