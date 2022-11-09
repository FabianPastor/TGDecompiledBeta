package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda233 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda233 INSTANCE = new MessagesController$$ExternalSyntheticLambda233();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda233() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processChannelsUpdatesQueue$266;
        lambda$processChannelsUpdatesQueue$266 = MessagesController.lambda$processChannelsUpdatesQueue$266((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processChannelsUpdatesQueue$266;
    }
}
