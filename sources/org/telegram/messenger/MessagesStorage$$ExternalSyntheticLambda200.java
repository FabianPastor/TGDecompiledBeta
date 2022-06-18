package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda200 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda200 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda200();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda200() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$125((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
