package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda98 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda98 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda98();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda98() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$41((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
