package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda193 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda193 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda193();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda193() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$120((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
