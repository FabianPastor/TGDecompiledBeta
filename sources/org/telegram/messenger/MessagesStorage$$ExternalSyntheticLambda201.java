package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda201 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda201();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$50((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
