package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda121 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda121 INSTANCE = new MessagesController$$ExternalSyntheticLambda121();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda121() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
