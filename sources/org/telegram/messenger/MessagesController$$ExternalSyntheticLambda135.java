package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda135 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda135 INSTANCE = new MessagesController$$ExternalSyntheticLambda135();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda135() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
