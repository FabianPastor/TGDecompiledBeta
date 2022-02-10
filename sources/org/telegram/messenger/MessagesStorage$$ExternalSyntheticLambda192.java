package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda192 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda192 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda192();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda192() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$44((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
