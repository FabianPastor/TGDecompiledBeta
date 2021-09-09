package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda187 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda187 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda187();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda187() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$loadDialogFilters$35((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
