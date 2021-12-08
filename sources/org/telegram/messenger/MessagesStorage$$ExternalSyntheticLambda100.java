package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda100 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda100 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda100();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda100() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$117((TLRPC.Message) obj, (TLRPC.Message) obj2);
    }
}
