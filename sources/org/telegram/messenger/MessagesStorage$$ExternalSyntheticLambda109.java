package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda109 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda109 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda109();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda109() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$44((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
