package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class MessagesStorage$$Lambda$108 implements Comparator {
    static final Comparator $instance = new MessagesStorage$$Lambda$108();

    private MessagesStorage$$Lambda$108() {
    }

    public int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$86$MessagesStorage((Message) obj, (Message) obj2);
    }
}
