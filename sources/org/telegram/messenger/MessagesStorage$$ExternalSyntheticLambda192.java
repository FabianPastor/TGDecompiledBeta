package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda192 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda192 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda192();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda192() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$119((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}
