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
        int lambda$processChannelsUpdatesQueue$260;
        lambda$processChannelsUpdatesQueue$260 = MessagesController.lambda$processChannelsUpdatesQueue$260((TLRPC$Updates) obj, (TLRPC$Updates) obj2);
        return lambda$processChannelsUpdatesQueue$260;
    }
}
