package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda228 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda228 INSTANCE = new MessagesController$$ExternalSyntheticLambda228();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda228() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$264;
        lambda$processUpdatesQueue$264 = MessagesController.lambda$processUpdatesQueue$264((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processUpdatesQueue$264;
    }
}
