package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda204 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda204 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda204();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda204() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$128((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
