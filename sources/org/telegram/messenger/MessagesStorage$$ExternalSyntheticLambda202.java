package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda202 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda202 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda202();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda202() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$126((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
