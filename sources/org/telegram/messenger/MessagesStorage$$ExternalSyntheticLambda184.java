package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda184 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda184 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda184();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda184() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$loadDialogFilters$32((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
