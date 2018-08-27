package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class MessagesController$$Lambda$114 implements Comparator {
    private final MessagesController arg$1;

    MessagesController$$Lambda$114(MessagesController messagesController) {
        this.arg$1 = messagesController;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$processUpdatesQueue$174$MessagesController((Updates) obj, (Updates) obj2);
    }
}
