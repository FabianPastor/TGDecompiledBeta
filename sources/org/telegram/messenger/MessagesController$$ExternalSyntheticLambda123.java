package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda123 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda123 INSTANCE = new MessagesController$$ExternalSyntheticLambda123();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda123() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
