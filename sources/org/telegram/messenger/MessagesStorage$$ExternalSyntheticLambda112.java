package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda112 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda112 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda112();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda112() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$125((TLRPC.Message) obj, (TLRPC.Message) obj2);
    }
}
