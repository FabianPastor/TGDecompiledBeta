package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Update;

final /* synthetic */ class MessagesController$$Lambda$1 implements Comparator {
    private final MessagesController arg$1;

    MessagesController$$Lambda$1(MessagesController messagesController) {
        this.arg$1 = messagesController;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$new$0$MessagesController((Update) obj, (Update) obj2);
    }
}
