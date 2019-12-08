package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$a4vtDwm_WzjIzdRrP9O9Os3QjuQ implements Comparator {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$a4vtDwm_WzjIzdRrP9O9Os3QjuQ(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$processUpdatesQueue$194$MessagesController((Updates) obj, (Updates) obj2);
    }
}
