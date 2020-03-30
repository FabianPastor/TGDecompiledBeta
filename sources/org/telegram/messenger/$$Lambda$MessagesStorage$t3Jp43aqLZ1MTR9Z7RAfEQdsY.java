package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesStorage$-t3Jp43aqLZ1-MTR9Z7RAfEQdsY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY INSTANCE = new $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY();

    private /* synthetic */ $$Lambda$MessagesStorage$t3Jp43aqLZ1MTR9Z7RAfEQdsY() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$35((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
