package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda232 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda232 INSTANCE = new MessagesController$$ExternalSyntheticLambda232();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda232() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$269;
        lambda$processUpdatesQueue$269 = MessagesController.lambda$processUpdatesQueue$269((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processUpdatesQueue$269;
    }
}
