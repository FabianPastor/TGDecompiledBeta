package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda210 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda210 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda210();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda210() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getMessagesInternal$131;
        lambda$getMessagesInternal$131 = MessagesStorage.lambda$getMessagesInternal$131((TLRPC$Message) obj, (TLRPC$Message) obj2);
        return lambda$getMessagesInternal$131;
    }
}
