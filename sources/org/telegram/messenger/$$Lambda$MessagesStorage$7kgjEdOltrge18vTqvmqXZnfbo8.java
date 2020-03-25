package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesStorage$7kgjEdOltrge18vTqvmqXZnfbo8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesStorage$7kgjEdOltrge18vTqvmqXZnfbo8 implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesStorage$7kgjEdOltrge18vTqvmqXZnfbo8 INSTANCE = new $$Lambda$MessagesStorage$7kgjEdOltrge18vTqvmqXZnfbo8();

    private /* synthetic */ $$Lambda$MessagesStorage$7kgjEdOltrge18vTqvmqXZnfbo8() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$processLoadedFilterPeersInternal$34((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}
