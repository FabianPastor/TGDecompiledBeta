package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda231 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda231 INSTANCE = new MessagesController$$ExternalSyntheticLambda231();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda231() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$266;
        lambda$processUpdatesQueue$266 = MessagesController.lambda$processUpdatesQueue$266((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processUpdatesQueue$266;
    }
}
