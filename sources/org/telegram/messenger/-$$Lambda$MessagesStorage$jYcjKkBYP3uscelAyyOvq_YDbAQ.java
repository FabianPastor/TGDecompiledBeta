package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$jYcjKkBYP3uscelAyyOvq_YDbAQ implements Runnable {
    private final /* synthetic */ ArrayList f$0;

    public /* synthetic */ -$$Lambda$MessagesStorage$jYcjKkBYP3uscelAyyOvq_YDbAQ(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersDidLoad, this.f$0);
    }
}
