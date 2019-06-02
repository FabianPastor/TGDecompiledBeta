package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$YABYJmoqkk8zjJ-LjhR8xe0JMBU implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ MessageObject f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$YABYJmoqkk8zjJ-LjhR8xe0JMBU(int i, MessageObject messageObject, ArrayList arrayList) {
        this.f$0 = i;
        this.f$1 = messageObject;
        this.f$2 = arrayList;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(this.f$1.getDialogId()), this.f$2);
    }
}
