package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda211 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda211 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda211();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda211() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getMessagesInternal$132;
        lambda$getMessagesInternal$132 = MessagesStorage.lambda$getMessagesInternal$132((TLRPC$Message) obj, (TLRPC$Message) obj2);
        return lambda$getMessagesInternal$132;
    }
}
