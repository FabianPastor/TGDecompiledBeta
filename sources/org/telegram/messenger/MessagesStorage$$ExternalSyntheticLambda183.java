package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda183 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda183 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda183();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda183() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$36((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
