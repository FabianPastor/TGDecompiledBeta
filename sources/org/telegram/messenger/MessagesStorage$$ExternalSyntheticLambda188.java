package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda188 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda188 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda188();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda188() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$loadDialogFilters$37((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}