package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda201 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda201();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$126((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
