package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda189 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda189 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda189();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda189() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$116((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
