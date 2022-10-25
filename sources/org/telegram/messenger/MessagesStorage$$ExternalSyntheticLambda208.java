package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda208 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda208 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda208();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda208() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$loadDialogFilters$45;
        lambda$loadDialogFilters$45 = MessagesStorage.lambda$loadDialogFilters$45((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
        return lambda$loadDialogFilters$45;
    }
}
