package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda140 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda140 INSTANCE = new MessagesController$$ExternalSyntheticLambda140();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda140() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
