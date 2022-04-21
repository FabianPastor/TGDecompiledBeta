package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda132 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda132 INSTANCE = new MessagesController$$ExternalSyntheticLambda132();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda132() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
