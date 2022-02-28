package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda203 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda203 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda203();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda203() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$52((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
