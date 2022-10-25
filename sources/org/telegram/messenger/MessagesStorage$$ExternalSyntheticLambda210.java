package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda210 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda210 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda210();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda210() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processLoadedFilterPeersInternal$50;
        lambda$processLoadedFilterPeersInternal$50 = MessagesStorage.lambda$processLoadedFilterPeersInternal$50((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
        return lambda$processLoadedFilterPeersInternal$50;
    }
}
