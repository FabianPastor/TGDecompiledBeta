package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda185 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda185 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda185();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda185() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$112((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
