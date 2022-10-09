package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda201 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda201();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getMessagesInternal$126;
        lambda$getMessagesInternal$126 = MessagesStorage.lambda$getMessagesInternal$126((TLRPC$Message) obj, (TLRPC$Message) obj2);
        return lambda$getMessagesInternal$126;
    }
}
