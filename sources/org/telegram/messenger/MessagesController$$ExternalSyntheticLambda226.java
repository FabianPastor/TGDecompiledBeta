package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda226 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda226 INSTANCE = new MessagesController$$ExternalSyntheticLambda226();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda226() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}
