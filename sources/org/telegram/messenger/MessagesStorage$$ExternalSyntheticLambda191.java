package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda191 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda191 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda191();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda191() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$loadDialogFilters$40((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
