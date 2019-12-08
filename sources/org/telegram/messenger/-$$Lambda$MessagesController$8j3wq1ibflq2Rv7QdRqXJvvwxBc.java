package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$8j3wq1ibflq2Rv7QdRqXJvvwxBc implements Comparator {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$8j3wq1ibflq2Rv7QdRqXJvvwxBc(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$processUpdatesQueue$208$MessagesController((Updates) obj, (Updates) obj2);
    }
}
