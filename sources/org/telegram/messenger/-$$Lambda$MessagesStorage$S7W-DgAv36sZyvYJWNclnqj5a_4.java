package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$S7W-DgAv36sZyvYJWNclnqj5a_4 implements Runnable {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$MessagesStorage$S7W-DgAv36sZyvYJWNclnqj5a_4(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersDidLoad, this.f$0);
    }
}
