package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda186 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda186 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda186();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda186() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$39((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
