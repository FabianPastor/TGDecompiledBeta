package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class MessagesStorage$$Lambda$103 implements Comparator {
    static final Comparator $instance = new MessagesStorage$$Lambda$103();

    private MessagesStorage$$Lambda$103() {
    }

    public int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$81$MessagesStorage((Message) obj, (Message) obj2);
    }
}
