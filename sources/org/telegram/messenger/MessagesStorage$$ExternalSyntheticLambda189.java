package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda189 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda189 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda189();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda189() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$41((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
