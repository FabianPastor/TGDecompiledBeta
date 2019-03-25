package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class MessagesStorage$$Lambda$111 implements Comparator {
    static final Comparator $instance = new MessagesStorage$$Lambda$111();

    private MessagesStorage$$Lambda$111() {
    }

    public int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$null$91$MessagesStorage((Message) obj, (Message) obj2);
    }
}
