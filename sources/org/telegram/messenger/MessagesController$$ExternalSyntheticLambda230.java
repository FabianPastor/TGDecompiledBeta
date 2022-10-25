package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda230 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda230 INSTANCE = new MessagesController$$ExternalSyntheticLambda230();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda230() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processChannelsUpdatesQueue$262;
        lambda$processChannelsUpdatesQueue$262 = MessagesController.lambda$processChannelsUpdatesQueue$262((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processChannelsUpdatesQueue$262;
    }
}
