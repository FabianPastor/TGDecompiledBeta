package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class MessagesStorage$$Lambda$112 implements Comparator {
    static final Comparator $instance = new MessagesStorage$$Lambda$112();

    private MessagesStorage$$Lambda$112() {
    }

    public int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$92$MessagesStorage((Message) obj, (Message) obj2);
    }
}
