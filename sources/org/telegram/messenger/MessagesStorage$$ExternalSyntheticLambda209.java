package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda209 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda209 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda209();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda209() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$loadDialogFilters$46;
        lambda$loadDialogFilters$46 = MessagesStorage.lambda$loadDialogFilters$46((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
        return lambda$loadDialogFilters$46;
    }
}
