package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda190 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda190 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda190();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda190() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$117((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
