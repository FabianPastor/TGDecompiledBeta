package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda106 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda106 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda106();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda106() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$120((TLRPC.Message) obj, (TLRPC.Message) obj2);
    }
}
