package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda197 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda197 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda197();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda197() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$loadDialogFilters$40((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
