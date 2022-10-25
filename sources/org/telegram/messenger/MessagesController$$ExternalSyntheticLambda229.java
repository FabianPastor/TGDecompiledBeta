package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda229 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda229 INSTANCE = new MessagesController$$ExternalSyntheticLambda229();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda229() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processUpdatesQueue$265;
        lambda$processUpdatesQueue$265 = MessagesController.lambda$processUpdatesQueue$265((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processUpdatesQueue$265;
    }
}
