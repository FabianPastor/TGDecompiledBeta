package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda234 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda234 INSTANCE = new MessagesController$$ExternalSyntheticLambda234();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda234() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$268;
        lambda$processUpdatesQueue$268 = MessagesController.lambda$processUpdatesQueue$268((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processUpdatesQueue$268;
    }
}
