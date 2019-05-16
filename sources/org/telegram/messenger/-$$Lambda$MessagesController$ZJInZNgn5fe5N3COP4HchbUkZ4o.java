package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ZJInZNgn5fe5N3COP4HchbUkZ4o implements Comparator {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$ZJInZNgn5fe5N3COP4HchbUkZ4o(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$processUpdatesQueue$192$MessagesController((Updates) obj, (Updates) obj2);
    }
}
