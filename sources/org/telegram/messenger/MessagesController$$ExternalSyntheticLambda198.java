package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Update;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda198 implements Comparator {
    public final /* synthetic */ MessagesController f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda198(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$new$3((TLRPC$Update) obj, (TLRPC$Update) obj2);
    }
}
